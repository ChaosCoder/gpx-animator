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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

final class GpxContentHandler extends DefaultHandler {
	
	private static final String ATTR_LON = "lon";
	private static final String ATTR_LAT = "lat";
	private static final String ELEM_TRKSEG = "trkseg";
	private static final String ELEM_TRKPT = "trkpt";
	private static final String ELEM_TIME = "time";
	
	private final List<List<LatLon>> timePointListList = new ArrayList<List<LatLon>>();
	private List<LatLon> timePointList;

	private final StringBuilder timeSb = new StringBuilder();
	private long time = Long.MIN_VALUE;
	private double lat;
	private double lon;
	

	@Override
	public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
		if (ELEM_TRKSEG.equals(qName)) {
			timePointList = new ArrayList<LatLon>();
		} else if (ELEM_TRKPT.equals(qName)) {
			lat = Double.parseDouble(attributes.getValue(ATTR_LAT));
			lon = Double.parseDouble(attributes.getValue(ATTR_LON));
		} else if (ELEM_TIME.equals(qName)) {
			timeSb.setLength(0);
		}
	}
	

	@Override
	public void characters(final char[] ch, final int start, final int length) throws SAXException {
		if (timeSb != null) {
			timeSb.append(ch, start, length);
		}
	}
	

	@Override
	public void endElement(final String uri, final String localName, final String qName) throws SAXException {
		if (ELEM_TRKSEG.equals(qName)) {
			timePointListList.add(timePointList);
			timePointList = null;
		} else if (ELEM_TRKPT.equals(qName)) {
			timePointList.add(new LatLon(lat, lon, time));
			time = Long.MIN_VALUE;
		} else if (ELEM_TIME.equals(qName)) {
			try {
				time = Utils.parseISO8601(timeSb.toString()).getTime();
			} catch (final ParseException e) {
				throw new SAXException(e);
			}
			timeSb.setLength(0);
		}
	}
	
	
	public List<List<LatLon>> getPointLists() {
		return timePointListList;
	}
	
}