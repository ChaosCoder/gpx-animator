package sk.freemap.gpxAnimator.ui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

public class TrackSettingsPanel extends JPanel {

	private static final long serialVersionUID = 2492074184123083022L;
	private final JLabel lblLabel;
	private final JTextField labelTextField;
	private final JLabel lblLineWidth;
	private final JLabel lblTimeOffset;
	private final JLabel lblForcedPointTime;
	private final JSpinner forcedPointTimeIntervalSpinner;
	private final JSpinner timeOffsetSpinner;
	private final JSpinner lineWidthSpinner;
	private final JLabel lblColor_1;
	private final ColorSelector colorSelector;
	private final FileSelector inputGpxFileSelector;


	public TrackSettingsPanel(final ActionListener removeActionListener) {
		setBounds(100, 100, 595, 419);
		setBorder(new EmptyBorder(5, 5, 5, 5));
		final GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		setLayout(gbl_contentPane);
		
		final JLabel lblGpx = new JLabel("Input GPX File");
		final GridBagConstraints gbc_lblGpx = new GridBagConstraints();
		gbc_lblGpx.insets = new Insets(0, 0, 5, 5);
		gbc_lblGpx.anchor = GridBagConstraints.EAST;
		gbc_lblGpx.gridx = 0;
		gbc_lblGpx.gridy = 0;
		add(lblGpx, gbc_lblGpx);
		
		inputGpxFileSelector = new FileSelector();
		final GridBagConstraints gbc_inputGpxFileSelector = new GridBagConstraints();
		gbc_inputGpxFileSelector.insets = new Insets(0, 0, 5, 0);
		gbc_inputGpxFileSelector.fill = GridBagConstraints.BOTH;
		gbc_inputGpxFileSelector.gridx = 1;
		gbc_inputGpxFileSelector.gridy = 0;
		add(inputGpxFileSelector, gbc_inputGpxFileSelector);
		
		lblLabel = new JLabel("Label");
		final GridBagConstraints gbc_lblLabel = new GridBagConstraints();
		gbc_lblLabel.anchor = GridBagConstraints.EAST;
		gbc_lblLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblLabel.gridx = 0;
		gbc_lblLabel.gridy = 1;
		add(lblLabel, gbc_lblLabel);
		
		labelTextField = new JTextField();
		final GridBagConstraints gbc_labelTextField = new GridBagConstraints();
		gbc_labelTextField.insets = new Insets(0, 0, 5, 0);
		gbc_labelTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_labelTextField.gridx = 1;
		gbc_labelTextField.gridy = 1;
		add(labelTextField, gbc_labelTextField);
		labelTextField.setColumns(10);
		
		lblColor_1 = new JLabel("Color");
		final GridBagConstraints gbc_lblColor_1 = new GridBagConstraints();
		gbc_lblColor_1.anchor = GridBagConstraints.EAST;
		gbc_lblColor_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblColor_1.gridx = 0;
		gbc_lblColor_1.gridy = 2;
		add(lblColor_1, gbc_lblColor_1);
		
		colorSelector = new ColorSelector();
		final GridBagConstraints gbc_colorSelector = new GridBagConstraints();
		gbc_colorSelector.insets = new Insets(0, 0, 5, 0);
		gbc_colorSelector.fill = GridBagConstraints.BOTH;
		gbc_colorSelector.gridx = 1;
		gbc_colorSelector.gridy = 2;
		add(colorSelector, gbc_colorSelector);
		
		lblLineWidth = new JLabel("Line Width");
		final GridBagConstraints gbc_lblLineWidth = new GridBagConstraints();
		gbc_lblLineWidth.insets = new Insets(0, 0, 5, 5);
		gbc_lblLineWidth.anchor = GridBagConstraints.EAST;
		gbc_lblLineWidth.gridx = 0;
		gbc_lblLineWidth.gridy = 3;
		add(lblLineWidth, gbc_lblLineWidth);
		
		lineWidthSpinner = new JSpinner();
		lineWidthSpinner.setFont(new Font("Dialog", Font.PLAIN, 12));
		lineWidthSpinner.setModel(new SpinnerNumberModel(new Double(0), new Double(0), null, new Double(1)));
		final GridBagConstraints gbc_lineWidthSpinner = new GridBagConstraints();
		gbc_lineWidthSpinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_lineWidthSpinner.insets = new Insets(0, 0, 5, 0);
		gbc_lineWidthSpinner.gridx = 1;
		gbc_lineWidthSpinner.gridy = 3;
		add(lineWidthSpinner, gbc_lineWidthSpinner);
		
		lblTimeOffset = new JLabel("Time Offset");
		final GridBagConstraints gbc_lblTimeOffset = new GridBagConstraints();
		gbc_lblTimeOffset.anchor = GridBagConstraints.EAST;
		gbc_lblTimeOffset.insets = new Insets(0, 0, 5, 5);
		gbc_lblTimeOffset.gridx = 0;
		gbc_lblTimeOffset.gridy = 4;
		add(lblTimeOffset, gbc_lblTimeOffset);
		
		timeOffsetSpinner = new JSpinner();
		final GridBagConstraints gbc_timeOffsetSpinner = new GridBagConstraints();
		gbc_timeOffsetSpinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_timeOffsetSpinner.insets = new Insets(0, 0, 5, 0);
		gbc_timeOffsetSpinner.gridx = 1;
		gbc_timeOffsetSpinner.gridy = 4;
		add(timeOffsetSpinner, gbc_timeOffsetSpinner);
		
		lblForcedPointTime = new JLabel("Forced Point Time Interval");
		final GridBagConstraints gbc_lblForcedPointTime = new GridBagConstraints();
		gbc_lblForcedPointTime.anchor = GridBagConstraints.EAST;
		gbc_lblForcedPointTime.insets = new Insets(0, 0, 5, 5);
		gbc_lblForcedPointTime.gridx = 0;
		gbc_lblForcedPointTime.gridy = 5;
		add(lblForcedPointTime, gbc_lblForcedPointTime);
		
		forcedPointTimeIntervalSpinner = new JSpinner();
		final GridBagConstraints gbc_forcedPointTimeIntervalSpinner = new GridBagConstraints();
		gbc_forcedPointTimeIntervalSpinner.insets = new Insets(0, 0, 5, 0);
		gbc_forcedPointTimeIntervalSpinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_forcedPointTimeIntervalSpinner.gridx = 1;
		gbc_forcedPointTimeIntervalSpinner.gridy = 5;
		add(forcedPointTimeIntervalSpinner, gbc_forcedPointTimeIntervalSpinner);
		
		if (removeActionListener != null) {
			final JButton btnNewButton = new JButton("Remove Track");
			btnNewButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					removeActionListener.actionPerformed(new ActionEvent(TrackSettingsPanel.this, ActionEvent.ACTION_PERFORMED, ""));
				}
			});
			final GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
			gbc_btnNewButton.anchor = GridBagConstraints.EAST;
			gbc_btnNewButton.gridwidth = 3;
			gbc_btnNewButton.insets = new Insets(0, 0, 0, 5);
			gbc_btnNewButton.gridx = 0;
			gbc_btnNewButton.gridy = 7;
			add(btnNewButton, gbc_btnNewButton);
		}
	}

}
