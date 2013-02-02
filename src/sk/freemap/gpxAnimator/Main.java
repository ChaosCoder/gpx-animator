/*
 *  Copyright 2013 Martin Ždila, Freemap Slovakia
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package sk.freemap.gpxAnimator;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

public class Main {

	private static final double MS = 1000d;
	private static final DateFormat DATE_FORMAT = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);

	private int margin = 20;
	private double speedup = 1000.0;
	private long tailDuration = 60 * 60;
	private double fps = 30.0;
	private boolean debug;
	private double totalTime = Double.NaN;
	private int width = 800;
	
	private long minTime = Long.MAX_VALUE;
	private final List<TreeMap<Long, Point2D>> timePointMapList = new ArrayList<TreeMap<Long,Point2D>>();
	
	private final List<String> inputGpxList = new ArrayList<String>();
	private String frameFilePattern = "frame%08d.png";

	private Font font;
	private FontMetrics fontMetrics;
	private int fontSize = 12;
	
	
	/**
	 * @param args
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 */
	public static void main(final String[] args) {
		final Main main = new Main(args);
		try {
			main.render();
		} catch (final UserException e) {
			if (main.debug) {
				e.printStackTrace();
			} else {
				System.err.println(e.getMessage());
			}
			System.exit(1);
		}
	}
	
	
	public Main(final String[] args) {
		for (int i = 0; i < args.length; i++) {
			final String arg = args[i];
			
			try {
				if (arg.equals("--input")) {
					inputGpxList.add(args[++i]);
				} else if (arg.equals("--output")) {
					frameFilePattern = args[++i];
				} else if (arg.equals("--margin")) {
					margin = Integer.parseInt(args[++i]);
				} else if (arg.equals("--speedup")) {
					speedup = Double.parseDouble(args[++i]);
				} else if (arg.equals("--tail-duration")) {
					tailDuration = Long.parseLong(args[++i]);
				} else if (arg.equals("--fps")) {
					fps = Double.parseDouble(args[++i]);
				} else if (arg.equals("--width")) {
					width = Integer.parseInt(args[++i]);
				} else if (arg.equals("--font-size")) {
					fontSize = Integer.parseInt(args[++i]);
				} else if (arg.equals("--debug")) {
					debug = true;
				} else if (arg.equals("--total-time")) {
					totalTime = Double.parseDouble(args[++i]);
				} else if (arg.equals("--help")) {
					printHelp();
				} else {
					System.err.println("unrecognised option " + arg);
					System.err.println("run program with --help option to print help");
					System.exit(1);
				}
			} catch (final NumberFormatException e) {
				System.err.println("invalid number for option " + arg);
				System.exit(1);
			} catch (final ArrayIndexOutOfBoundsException e) {
				System.err.println("missing parameter for option " + arg);
				System.exit(1);
			}
		}
	}


	private void printHelp() {
		System.out.println("GPX Animator 0.2");
		System.out.println("Copyright 2013 Martin Ždila, Freemap Slovakia");
		System.out.println();
		System.out.println("Usage:");
		System.out.println("--help");
		System.out.println("\tthis help");
		System.out.println("--input <input>");
		System.out.println("\tinput GPX filename; can be provided multiple times for multiple tracks");
		System.out.println("--output <output>");
		System.out.println("\toutput filename template for saved frames; default frame%08d.png");
		System.out.println("--tail-duration <time>");
		System.out.println("\tatest time of highlighted tail in seconds; default 3600");
		System.out.println("--margin <margin>");
		System.out.println("\tmargin in pixels; default 20");
		System.out.println("--speedup <speedup>");
		System.out.println("\tspeed multiplication of the real time; default 1000.0; complementary to --total-time option");
		System.out.println("--total-time <time>");
		System.out.println("\ttotal length of video in seconds; complementary to --speedup option");
		System.out.println("--fps <fps>");
		System.out.println("\tframes per second; default 30.0");
		System.out.println("--width <width>");
		System.out.println("\tvideo width in pixels; default 800");
		System.out.println("--font-size <size>");
		System.out.println("\tdatetime text font size; default 12; set to 0 for no date text");
		System.out.println("--debug");
		System.out.println("\ttoggle debugging");
	}

	
	private void validateOptions() throws UserException {
		if (inputGpxList.isEmpty()) {
			throw new UserException("missing input file");
		}
		
		// TODO other validations
	}
	
	
	private static TreeMap<Long, Point2D> parseGpx(final String inputGpx) throws UserException {
		final SAXParser saxParser;
		try {
			saxParser = SAXParserFactory.newInstance().newSAXParser();
		} catch (final ParserConfigurationException e) {
			throw new RuntimeException("can't create XML parser", e);
		} catch (final SAXException e) {
			throw new RuntimeException("can't create XML parser", e);
		}
		
		final GpxContentHandler dh = new GpxContentHandler();
		try {
			saxParser.parse(new File(inputGpx), dh);
		} catch (final SAXException e) {
			throw new UserException("error parsing input GPX file", e);
		} catch (final IOException e) {
			throw new UserException("error reading input file", e);
		} catch (final RuntimeException e) {
			throw new RuntimeException("internal error when parsing GPX file", e);
		}
		
		return dh.getTimePointMap();
	}
	
	
	private void render() throws UserException {
		validateOptions();
		
		double minX = Double.MAX_VALUE, maxX = Double.MIN_VALUE, minY = Double.MAX_VALUE, maxY = Double.MIN_VALUE;
		
		for (final String inputGpx : inputGpxList) {
			final TreeMap<Long, Point2D> timePointMap = parseGpx(inputGpx);
			timePointMapList.add(timePointMap);
			
			for (final Point2D point : timePointMap.values()) {
				final double x = point.getX();
				final double y = point.getY();
				
				minX = Math.min(x, minX);
				minY = Math.min(y, minY);
				maxX = Math.max(x, maxX);
				maxY = Math.max(y, maxY);
			}
		}
		
		final double scale = (width - margin * 2) / (maxX - minX);

		for (final TreeMap<Long, Point2D> timePointMap : timePointMapList) {
			for (final Point2D point : timePointMap.values()) {
				point.setLocation((point.getX() - minX) * scale, (maxY - point.getY()) * scale);
			}
		}
		
		final BufferedImage bi = new BufferedImage(
				(int) ((maxX - minX) * scale + margin * 2),
				(int) ((maxY - minY) * scale + margin * 2),
				BufferedImage.TYPE_INT_RGB);
		
		final Graphics2D ga = (Graphics2D) bi.getGraphics();
		ga.setColor(Color.white);
		ga.fillRect(0, 0, bi.getWidth(), bi.getHeight());
		
		if (fontSize > 0) {
			font = new Font(Font.MONOSPACED, Font.PLAIN, fontSize);
			fontMetrics = ga.getFontMetrics(font);
		}

		long maxTime = Long.MIN_VALUE;
		
		for (final TreeMap<Long, Point2D> timePointMap : timePointMapList) {
			maxTime = Math.max(maxTime, timePointMap.lastKey());
			minTime = Math.min(minTime, timePointMap.firstKey());
		}
		
		if (!Double.isNaN(totalTime)) {
			speedup = (maxTime - minTime) / totalTime;
		}

		final int frames = (int) ((maxTime + tailDuration * 1000 - minTime) * fps / (MS * speedup));
		
		System.out.println("To encode generated frames you may run this command:");
		System.out.println("ffmpeg -i " + frameFilePattern + " -vcodec mpeg4 -b 500k -r " + fps + " video.avi");
		
		for (int frame = 1; frame < frames; frame++) {
			System.out.println("Frame: " + frame + "/" + frames);
			paint(bi, frame, 0);
			
			final BufferedImage bi2 = Utils.deepCopy(bi);
			paint(bi2, frame, tailDuration * 1000);
			drawMarker(bi2, frame);
			
			if (fontSize > 0) {
				drawTime(bi2, frame);
			}
			
			final File outputfile = new File(String.format(frameFilePattern, frame));
		    try {
				ImageIO.write(bi2, "png", outputfile);
			} catch (final IOException e) {
				throw new UserException("error writing frame to " + outputfile);
			}
		}
	}


	private void drawTime(final BufferedImage bi2, final int frame) {
		final Graphics2D g2 = (Graphics2D) bi2.getGraphics();
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setColor(Color.BLACK);
		g2.setFont(font);

		final String dateString = DATE_FORMAT.format(new Date(getTime(frame)));
		g2.drawString(dateString, width - fontMetrics.stringWidth(dateString) - margin, bi2.getHeight() - margin);
	}


	private void drawMarker(final BufferedImage bi, final int frame) {
		final Graphics2D g2 = (Graphics2D) bi.getGraphics();
		g2.translate(margin, margin);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		final long t2 = getTime(frame);
		
		for (final TreeMap<Long, Point2D> timePointMap : timePointMapList) {
			final Entry<Long, Point2D> ceilingEntry = timePointMap.ceilingEntry(t2);
			final Entry<Long, Point2D> floorEntry = timePointMap.floorEntry(t2);
			if (ceilingEntry == null || floorEntry == null) {
				continue;
			}
			final Point2D p = floorEntry.getValue();
			g2.setColor(Color.green);
			final Ellipse2D.Double marker = new Ellipse2D.Double(p.getX() - 4.0, p.getY() - 4.0, 9.0, 9.0);
			g2.fill(marker);
			g2.setColor(Color.blue);
			g2.draw(marker);
		}
	}
	

	private void paint(final BufferedImage bi, final int frame, final long backTime) {
		final Graphics2D ga = (Graphics2D) bi.getGraphics();
		ga.translate(margin, margin);
		ga.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		ga.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
		final long time = getTime(frame);
		
		for (final TreeMap<Long, Point2D> timePointMap : timePointMapList) {
			final Long toTime = timePointMap.floorKey(time);
			
			if (toTime == null) {
				continue;
			}
			
			Point2D prevPoint = null;

			if (backTime == 0) {
				final long time2 = getTime(frame - 1);
				final Long fromTime = timePointMap.floorKey(time2);
				if (fromTime == null) {
					continue;
				}

				final NavigableMap<Long, Point2D> subMap = timePointMap.subMap(fromTime, true, toTime, true);
				ga.setPaint(Color.getHSBColor(240f / 360f, 0.25f, 1f));
				for (final Entry<Long, Point2D> entry: subMap.entrySet()) {
					if (prevPoint != null) {
						ga.draw(new Line2D.Double(prevPoint, entry.getValue()));
					}
					prevPoint = entry.getValue();
				}
			} else {
				final NavigableMap<Long, Point2D> subMap = timePointMap.subMap(toTime - backTime, true, toTime, true);
				for (final Entry<Long, Point2D> entry: subMap.entrySet()) {
					if (prevPoint != null) {
						final float ratio = (backTime - time + entry.getKey()) * 1f / backTime;
						if (ratio > 0) {
							ga.setPaint(Color.getHSBColor(240f / 360f, 0.25f + 0.75f * ratio, 1f - ratio));
							ga.draw(new Line2D.Double(prevPoint, entry.getValue()));
						}
					}
					prevPoint = entry.getValue();
				}
				
			}
		}
	}


	private long getTime(final int frame) {
		return (long) Math.floor(minTime + frame / fps * MS * speedup);
	}
	
}
