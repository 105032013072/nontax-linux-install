package com.bosssoft.install.nontax.linux.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.MainFrameController;
import com.bosssoft.platform.installer.core.gui.AbstractControlPanel;
import com.bosssoft.platform.installer.core.gui.AbstractSetupPanel;
import com.bosssoft.platform.installer.core.util.I18nUtil;
import com.bosssoft.platform.installer.wizard.gui.component.StepTitleLabel;

public class DigestPanel extends AbstractSetupPanel {
	Logger logger = Logger.getLogger(getClass());

	private StepTitleLabel line = new StepTitleLabel();

	private JTree treeDigest = new JTree();
	private DefaultTreeModel treeModel = null;
	private JTextArea introduction = new JTextArea();
	private JScrollPane jScrollPane1 = new JScrollPane();

	public DigestPanel() {
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void jbInit() throws Exception {
		setLayout(null);
		setOpaque(false);
		this.line.setText(I18nUtil.getString("STEP.DIGEST"));
		this.line.setBounds(new Rectangle(26, 5, 581, 27));

		this.introduction.setBackground(Color.white);
		this.introduction.setOpaque(false);
		this.introduction.setText(I18nUtil.getString("DIGEST.LABEL"));
		this.introduction.setEditable(false);
		this.introduction.setLineWrap(true);
		this.introduction.setRows(1);
		this.introduction.setWrapStyleWord(true);
		this.introduction.setBackground(Color.white);
		this.introduction.setBounds(new Rectangle(37, 43, 372, 54));
		this.jScrollPane1.setBounds(new Rectangle(37, 101, 372, 256));

		this.treeDigest.setShowsRootHandles(false);
		DefaultTreeCellRenderer render = new DefaultTreeCellRenderer();
		render.setLeafIcon(null);
		render.setOpenIcon(null);
		render.setClosedIcon(null);
		render.setDisabledIcon(null);
		render.setFont(new Font("Dialog", 0, 12));
		this.treeDigest.setRowHeight(20);
		this.treeDigest.setCellRenderer(render);

		setOpaque(false);
		add(this.line, null);
		add(this.introduction, null);
		add(this.jScrollPane1, null);
		this.jScrollPane1.getViewport().add(this.treeDigest, null);
	}

	public void afterShow() {
	}

	public void beforeNext() {
		AbstractControlPanel controlPane = MainFrameController.getControlPanel();
		controlPane.setButtonText("next", I18nUtil.getString("BUTTON_NEXT"));
	}

	public void beforePrevious() {
	}

	public void beforeShow() {
		AbstractControlPanel controlPane = MainFrameController.getControlPanel();
		controlPane.setButtonVisible("finish", false);
		controlPane.setButtonVisible("help", false);

		controlPane.setButtonText("next", I18nUtil.getString("BUTTON_INSTALL"));

		loadTree();
		expandAllNodes(this.treeDigest);
	}

	private void loadTree() {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(I18nUtil.getString("DIGEST.LABEL.ROOT"));
		this.treeModel = new DefaultTreeModel(root);
		this.treeDigest.setModel(this.treeModel);

		String productLabel = I18nUtil.getString("DIGEST.LABEL.PRODUCT");
		String installDirLabel = I18nUtil.getString("DIGEST.LABEL.INSTALLDIR");
		String optionsLabel = I18nUtil.getString("DIGEST.LABEL.OPTIONS");
		String dbLabel = I18nUtil.getString("DIGEST.LABEL.DB");
		String asLabel = I18nUtil.getString("DIGEST.LABEL.APPSVR");
		String modelLabel = I18nUtil.getString("DIGEST.LABEL.MODEL");
		

		DefaultMutableTreeNode labelNode = null;
		labelNode = new DefaultMutableTreeNode(productLabel);
		this.treeModel.insertNodeInto(labelNode, root, root.getChildCount());
		this.treeModel.insertNodeInto(new DefaultMutableTreeNode(I18nUtil.getString("PRODUCT." + getContext().getStringValue("EDITION").toUpperCase())), labelNode, 0);

		labelNode = new DefaultMutableTreeNode(installDirLabel);
		this.treeModel.insertNodeInto(labelNode, root, root.getChildCount());
		boolean is_was_nd = Boolean.valueOf(getContext().getStringValue("IS_CLUSTER")).booleanValue();

		if (!is_was_nd) {
			this.treeModel.insertNodeInto(new DefaultMutableTreeNode(getContext().getStringValue("INSTALL_DIR")), labelNode, labelNode.getChildCount());
			labelNode = new DefaultMutableTreeNode(optionsLabel);
			this.treeModel.insertNodeInto(labelNode, root, root.getChildCount());

			List keys = getContext().getKeysLike("OPTION.");
			String key = null;
			if (keys.size() > 0) {
				int i = 0;
				for (int j = keys.size(); i < j; i++) {
					key = keys.get(i).toString();
					if (this.context.getStringValue(key).toLowerCase().trim().equals("true")) {
						this.treeModel.insertNodeInto(new DefaultMutableTreeNode(I18nUtil.getString(key)), labelNode, labelNode.getChildCount());
					}

				}

			}

			String strNamekeys = getContext().getStringValue("OPTIONS.NAMEKEYS");
			String[] namekeys = strNamekeys.split(",");
			String fullNamekey = null;
			DefaultMutableTreeNode parent = null;
			int i = 0;
			for (int j = namekeys.length; i < j; i++) {
				fullNamekey = namekeys[i];
				if ((fullNamekey != null) && (!fullNamekey.equals(""))) {
					if (fullNamekey.indexOf(".") < 0) {
						this.treeModel.insertNodeInto(new DefaultMutableTreeNode(fullNamekey), labelNode, labelNode.getChildCount());
					} else {
						String[] levels = fullNamekey.split("\\.");
						String level = null;
						parent = labelNode;
						int k = 0;
						for (int l = levels.length - 1; k < l; k++) {
							level = levels[k];
							if ((level != null) && (!level.equals("")))
								parent = getChildByNameKey(parent, level);
						}
						this.treeModel.insertNodeInto(new DefaultMutableTreeNode(levels[(levels.length - 1)]), parent, parent.getChildCount());
					}
				}
			}
		}
		labelNode = new DefaultMutableTreeNode(dbLabel);
		this.treeModel.insertNodeInto(labelNode, root, root.getChildCount());
		this.treeModel.insertNodeInto(new DefaultMutableTreeNode(getContext().getStringValue("DB_TYPE") + getContext().getStringValue("DB_VERSION")), labelNode, 0);

		labelNode = new DefaultMutableTreeNode(asLabel);
		this.treeModel.insertNodeInto(labelNode, root, root.getChildCount());
		this.treeModel.insertNodeInto(new DefaultMutableTreeNode(getContext().getStringValue("APP_SERVER_TYPE")), labelNode, 0);
		
		labelNode = new DefaultMutableTreeNode(modelLabel);
		this.treeModel.insertNodeInto(labelNode, root, root.getChildCount());
		this.treeModel.insertNodeInto(new DefaultMutableTreeNode(getContext().getStringValue("MODULE_OPTIONS_NAMES")), labelNode, 0);
	}

	private DefaultMutableTreeNode getChildByNameKey(DefaultMutableTreeNode parent, String nameKey) {
		DefaultMutableTreeNode child = null;
		Enumeration enume = parent.children();
		while (enume.hasMoreElements()) {
			child = (DefaultMutableTreeNode) enume.nextElement();
			if (((String) child.getUserObject()).equals(nameKey))
				return child;
		}
		return child;
	}

	public void expandAllNodes(JTree tree) {
		int old = 0;
		int now = 0;
		do {
			old = tree.getRowCount();
			for (int i = 0; i < old; i++) {
				tree.expandRow(i);
			}
			now = tree.getRowCount();
		} while (now > old);
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
		if (getContext().getStringValue("INSTALLED_PROJECT").equalsIgnoreCase("false"))
			MainFrameController.showMessageDialog(I18nUtil.getString("PROJECT.WEBAPP.EXIST"), I18nUtil.getString("DIALOG.TITLE.WARNING"), 0);
	}
}