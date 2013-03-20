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
package sk.freemap.gpxAnimator.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileSelector extends JPanel {
	private static final long serialVersionUID = 3157365691996396016L;
	
	private final JTextField fileTextField;

	/**
	 * Create the panel.
	 */
	public FileSelector() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		fileTextField = new JTextField();
		fileTextField.setMaximumSize(new Dimension(2147483647, 21));
		fileTextField.setPreferredSize(new Dimension(55, 21));
		add(fileTextField);
		fileTextField.setColumns(10);
		
		fileTextField.getDocument().addDocumentListener(new DocumentListener() {
			String oldText = fileTextField.getText();
			
			@Override
			public void removeUpdate(final DocumentEvent e) {
				fire();
			}
			
			@Override
			public void insertUpdate(final DocumentEvent e) {
				fire();
			}
			
			@Override
			public void changedUpdate(final DocumentEvent e) {
				fire();
			}
			
			private void fire() {
				firePropertyChange("filename", oldText, oldText = fileTextField.getText());
			}
		});
		
		final Component rigidArea = Box.createRigidArea(new Dimension(5, 0));
		add(rigidArea);
		
		final JButton btnNewButton = new JButton("Browse");
		btnNewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				final JFileChooser gpxFileChooser = new JFileChooser();
				final FileFilter filter = new FileNameExtensionFilter("GPX Files", "gpx");
				gpxFileChooser.setAcceptAllFileFilterUsed(false);
				gpxFileChooser.addChoosableFileFilter(filter);
				if (gpxFileChooser.showOpenDialog(FileSelector.this) == JFileChooser.APPROVE_OPTION) {
					setFilename(gpxFileChooser.getSelectedFile().toString());
				}
				
//				final JFileChooser chooser = new JFileChooser();
//				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//				chooser.showOpenDialog(MainFrame.this);
			}
		});
		add(btnNewButton);

	}

	public String getFilename() {
		return fileTextField.getText();
	}
	
	public void setFilename(final String filename) {
		fileTextField.setText(filename);
	}

}
