package org.abapgit.adt.ui.internal.dialogs;

import org.abapgit.adt.backend.model.abapgitexternalrepo.IExternalRepositoryInfoRequest;
import org.abapgit.adt.backend.model.abapgitexternalrepo.impl.AbapgitexternalrepoFactoryImpl;
import org.abapgit.adt.ui.internal.i18n.Messages;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AbapGitStagingCredentialsDialog extends TitleAreaDialog {

	protected Text usernameField;
	protected Text passwordField;

	private String username;
	private String password;

	private String errorMessage;

	private Button storeCredentialCheckBox;

	private boolean storeCredsInSecStore = false;

	public AbapGitStagingCredentialsDialog(Shell parentShell, String errorMessage) {
		super(parentShell);
		this.errorMessage = errorMessage;
	}

	public AbapGitStagingCredentialsDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	public void create() {
		super.create();
		setTitle(Messages.AbapGitStaging_credentials_dialog_title);
		if (this.errorMessage != null) {
			setMessage(this.errorMessage, IMessageProvider.ERROR);
		} else {
			setMessage(Messages.AbapGitStaging_credentials_dialog_desc, IMessageProvider.INFORMATION);
		}
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite credentialsComposite = new Composite(parent, SWT.NONE);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		credentialsComposite.setLayoutData(gd);
		GridLayout layout = new GridLayout(2, false);
		credentialsComposite.setLayout(layout);

		Label usernameLabel = new Label(credentialsComposite, SWT.NONE);
		usernameLabel.setText(Messages.AbapGitStaging_credentials_dialog_username);

		this.usernameField = new Text(credentialsComposite, SWT.SINGLE);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		this.usernameField.setLayoutData(data);

		Label passwordLabel = new Label(credentialsComposite, SWT.NONE);
		passwordLabel.setText(Messages.AbapGitStaging_credentials_dialog_password);

		this.passwordField = new Text(credentialsComposite, SWT.SINGLE | SWT.PASSWORD);
		data = new GridData(GridData.FILL_HORIZONTAL);
		this.passwordField.setLayoutData(data);

		this.usernameField.addModifyListener(event -> {
			validateAndSetMessage();
		});

		this.passwordField.addModifyListener(event -> {
			validateAndSetMessage();
		});

		// Check Box to store credentials in secure storage

		this.storeCredentialCheckBox = new Button(credentialsComposite, SWT.CHECK);
		this.storeCredentialCheckBox.setText(Messages.AbapGitStagingCredentialsDialog_label_store_in_secure_store);
		GridDataFactory.swtDefaults().applyTo(this.storeCredentialCheckBox);

		this.storeCredentialCheckBox.setSelection(this.storeCredsInSecStore);

		this.storeCredentialCheckBox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				Button chbox = (Button) event.getSource();
				AbapGitStagingCredentialsDialog.this.storeCredsInSecStore = chbox.getSelection();
			}

		});

		return credentialsComposite;
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	public void okPressed() {
		if (validateAndSetMessage()) {
			super.okPressed();
		}
	}

	private boolean validateAndSetMessage() {
		this.username = this.usernameField.getText().trim();
		if (this.username.isEmpty()) {
			setMessage(Messages.AbapGitStaging_credentials_dialog_error_invalid_username, IMessageProvider.INFORMATION);
			return false;
		}

		this.password = this.passwordField.getText();
		if (this.password.isEmpty()) {
			setMessage(Messages.AbapGitStaging_credentials_dialog_error_invalid_password, IMessageProvider.INFORMATION);
			return false;
		}

		setMessage(null);
		return true;
	}

	public IExternalRepositoryInfoRequest getExternalRepoInfo() {
		IExternalRepositoryInfoRequest info = AbapgitexternalrepoFactoryImpl.eINSTANCE.createExternalRepositoryInfoRequest();
		info.setUser(this.username);
		info.setPassword(this.password);
		return info;
	}

	public boolean storeInSecureStorage() {
		return this.storeCredsInSecStore;
	}

	public void setStoreInSecureStorage(boolean selected) {
		this.storeCredsInSecStore = selected;
	}
}
