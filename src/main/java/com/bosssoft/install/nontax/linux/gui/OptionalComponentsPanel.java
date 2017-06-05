package com.bosssoft.install.nontax.linux.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.border.TitledBorder;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.bosssoft.platform.installer.core.MainFrameController;
import com.bosssoft.platform.installer.core.gui.AbstractControlPanel;
import com.bosssoft.platform.installer.core.gui.AbstractSetupPanel;
import com.bosssoft.platform.installer.core.option.CompDef;
import com.bosssoft.platform.installer.core.option.ComponentsDefHelper;
import com.bosssoft.platform.installer.core.option.ModuleDef;
import com.bosssoft.platform.installer.core.util.I18nUtil;
import com.bosssoft.platform.installer.wizard.gui.component.StepTitleLabel;
import com.bosssoft.platform.installer.wizard.gui.option.CheckBoxNodeEditor;
import com.bosssoft.platform.installer.wizard.gui.option.CheckBoxNodeRenderer;

public class OptionalComponentsPanel extends AbstractSetupPanel implements TreeSelectionListener{
	private static final long serialVersionUID = -9137406061783383805L;
	private BorderLayout borderLayout1 = new BorderLayout();

	private StepTitleLabel line = new StepTitleLabel();
	private JPanel setupPane = new JPanel();
	private JTree optionsTree = null;
	private JScrollPane scrollPane = new JScrollPane();

	private DefaultTreeModel treeModel = null;

	private JTextArea introduction = new JTextArea();
	private JTextArea compDesc = new JTextArea();

	private JLabel labelAllSize = new JLabel();
	private JLabel labelAllSizeValue = new JLabel("55MB");

	public OptionalComponentsPanel() {
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void jbInit() throws Exception {
		setLayout(this.borderLayout1);
		setOpaque(false);
		this.setupPane.setLayout(null);
		this.line.setText(I18nUtil.getString("STEP.CHOOSE.COMPONENT"));
		this.line.setBounds(new Rectangle(16, 5, 581, 27));

		this.introduction.setOpaque(false);
		this.introduction.setText(I18nUtil.getString("CHOOSE.MODULE.LABEL"));
		this.introduction.setEditable(false);
		this.introduction.setLineWrap(true);
		this.introduction.setRows(1);
		this.introduction.setWrapStyleWord(true);

		this.labelAllSize.setText(I18nUtil.getString("OPTIONS.LABEL.SIZENEEDED"));

		this.compDesc.setBackground(Color.WHITE);
		this.compDesc.setOpaque(false);
		this.compDesc.setEditable(false);
		this.compDesc.setLineWrap(true);
		this.compDesc.setWrapStyleWord(true);
		this.compDesc.setBorder(new TitledBorder(I18nUtil.getString("OPTIONS.LABEL.DESC")));
		this.labelAllSizeValue.setHorizontalAlignment(4);

		this.introduction.setBounds(new Rectangle(27, 32, 372, 30));
		this.scrollPane.setBounds(new Rectangle(27, 32, 382, 233));
		this.compDesc.setBounds(new Rectangle(27, 270, 382, 95));
		this.compDesc.setAlignmentY(0.0F);
		this.labelAllSize.setBounds(new Rectangle(27, 362, 150, 29));
		this.labelAllSizeValue.setBounds(new Rectangle(200, 362, 200, 29));

		add(this.setupPane, "Center");
		this.setupPane.setOpaque(false);
		this.setupPane.add(this.line, null);
		this.setupPane.add(this.scrollPane, null);
		this.setupPane.add(this.compDesc, null);
		this.setupPane.add(this.labelAllSize, null);
		this.setupPane.add(this.labelAllSizeValue, null);

		initTree();
		this.scrollPane.getViewport().add(this.optionsTree, null);
	}

	private void initTree() {
		List optionsList = ComponentsDefHelper.getOptionCompsDef();
		if ((optionsList == null) || (optionsList.size() == 0)) {
			return;
		}

		ModuleDef[] options = (ModuleDef[]) optionsList.toArray(new ModuleDef[0]);
		Arrays.sort(options);

		String rootUserObject = new String(I18nUtil.getString("STEP.CHOOSE.COMPONENT"));
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(rootUserObject);

		this.treeModel = new DefaultTreeModel(rootNode);
		this.optionsTree = new JTree(this.treeModel);

		DefaultMutableTreeNode child = null;
		for (ModuleDef oc : options) {
			child = getCompTreeNode(oc);
			if (child != null) {
				this.treeModel.insertNodeInto(child, rootNode, rootNode.getChildCount());
			}
		}
		ActionListener chooseListener = new ChooseActionListener();
		CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer(this.optionsTree, chooseListener);
		this.optionsTree.setCellRenderer(renderer);

		this.optionsTree.setCellEditor(new CheckBoxNodeEditor(this.optionsTree, chooseListener));
		this.optionsTree.setEditable(true);
		this.optionsTree.addTreeSelectionListener(this);
		this.optionsTree.addMouseListener(new TreeMouseListener());
		for (int i = 0; i < this.optionsTree.getRowCount(); i++) {
			this.optionsTree.expandRow(i);
		}
		this.optionsTree.setRowHeight(20);
		this.optionsTree.setToolTipText("Choose Option Component");
		this.optionsTree.setSelectionPath(new TreePath(rootNode.getPath()));
		calculateTotalSize();
	}

	private DefaultMutableTreeNode getCompTreeNode(CompDef comp) {
		DefaultMutableTreeNode node = null;

		node = new DefaultMutableTreeNode(comp);
		List<CompDef> children = comp.getComps();

		if ((children != null) && (children.size() > 0)) {
			DefaultMutableTreeNode child = null;
			for (CompDef oc : children) {
				child = getCompTreeNode(oc);
				if (child != null) {
					this.treeModel.insertNodeInto(child, node, node.getChildCount());
				}
			}
		}
		return node;
	}

	public void afterShow() {
	}

	public void beforeNext() {
		List<ModuleDef> optionsCompList = ComponentsDefHelper.getOptionCompsDef();
		List selectedComps = new ArrayList<CompDef>();
		StringBuffer optionsNamekeys = new StringBuffer();
		for (CompDef comp : optionsCompList) {
			getChoosedOptions(comp, "",selectedComps,optionsNamekeys);
		}

		this.context.setValue("MODULE_OPTIONS", selectedComps);
		this.context.setValue("MODULE_OPTIONS_NAMES", optionsNamekeys.toString());
	}

	private void getChoosedOptions(CompDef comp, String prefix,List options,StringBuffer optionsNamekeys) {
		String namekey = comp.getNameKey();
		if (comp.isSelected()) {
			options.add(comp);
			optionsNamekeys.append(prefix + namekey).append(",");
		}
	}

	public void beforePrevious() {
	}

	public void beforeShow() {
		AbstractControlPanel controlPane = MainFrameController.getControlPanel();
		controlPane.setButtonVisible("finish", false);
		controlPane.setButtonVisible("help", false);
	}

	public boolean checkInput() {
		return true;
	}

	public String getNextBranchID() {
		return "";
	}

	public void initialize(String[] parameters) {
	}

	public void afterActions() {
	}

	private void calculateTotalSize() {
		List<ModuleDef> optionsCompList = ComponentsDefHelper.getOptionCompsDef();
		double total = 0.0D;
		for (CompDef comp : optionsCompList) {
			total += getSelectedOptionSize(comp);
		}
		String totalStr = String.valueOf(total);
		int dotIndex = totalStr.lastIndexOf(".");
		if (totalStr.length() - dotIndex > 2)
			totalStr = totalStr.substring(0, dotIndex + 3);
		this.labelAllSizeValue.setText(totalStr + "MB");
	}

	private double getSelectedOptionSize(CompDef comp) {
		double size = 0.0D;

		if (comp.isSelected()) {
			List<CompDef> subComps = comp.getComps();
			if ((subComps != null) && (subComps.size() > 0)) {
				for (CompDef subComp : subComps)
					size += getSelectedOptionSize(subComp);
			} else {
				String compSize = comp.getSize();
				if ((compSize == null) || (compSize.trim().equals("")))
					compSize = "0";
				size = Double.parseDouble(compSize);
			}
		}

		return size;
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		JPanel panel = new OptionalComponentsPanel();

		frame.getContentPane().add(panel);
	}

	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) this.optionsTree.getLastSelectedPathComponent();
		String description = null;
		if (selectedNode.isRoot())
			description = I18nUtil.getString("OPTION.BPS_SUITE");
		else {
			description = I18nUtil.getString(((CompDef) selectedNode.getUserObject()).getDescKey());
		}

		this.compDesc.setText(description);
	}

	public class ChooseActionListener implements ActionListener {
		public ChooseActionListener() {
		}

		public void actionPerformed(ActionEvent ae) {
			OptionalComponentsPanel.this.calculateTotalSize();
		}
	}

	public class TreeMouseListener extends MouseInputAdapter {
		public TreeMouseListener() {
		}

		public void mouseEntered(MouseEvent e) {
			e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}
	}
}
