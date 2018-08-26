/**
 * This file is part of TKConfig application.
 *
 * Copyright (C) 2018 Claudiu Ciobotariu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ro.ciubex.tkconfig.activities;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import ro.ciubex.tkconfig.R;
import ro.ciubex.tkconfig.TKConfigApplication;
import ro.ciubex.tkconfig.dialogs.EditorDialog;
import ro.ciubex.tkconfig.dialogs.ParameterEditor;
import ro.ciubex.tkconfig.list.CommandListAdapter;
import ro.ciubex.tkconfig.list.ParamListAdapter;
import ro.ciubex.tkconfig.models.Command;
import ro.ciubex.tkconfig.models.GpsContact;
import ro.ciubex.tkconfig.models.Utilities;

/**
 * The main activity which should load and show the commands.
 *
 * @author Claudiu Ciobotariu
 */
public class TKConfigActivity extends BaseActivity {
    private CommandListAdapter adapter;
    private ListView commandsList;

    private final int CONFIRM_ID_DELETE = 0;
    private final int CONFIRM_ID_SMS_SEND = 1;
    private final int CONFIRM_ID_PARAMETERS = 2;
    private final int CONFIRM_ID_DONATE = 3;
    private final int SMS_NO_CONTACT = 4;
    private final int NO_PARAMS_TO_EDIT = 5;

    private static final int REQUEST_CODE_SETTINGS = 0;
    private static final int REQUEST_CODE_ABOUT = 1;
    private static final int PERMISSIONS_REQUEST_CODE = 44;
    private boolean invokedFromShortcut;

    private enum METHOD {
        NOTHING, SEND_SMS
    }

    /**
     * The method invoked when the activity is creating
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            invokedFromShortcut = "true".equalsIgnoreCase(bundle.getString("invokedFromShortcut"));
        }
        if (!invokedFromShortcut) {
            setMenuId(R.menu.activity_config);
            prepareMainListView();
        }
    }

    /**
     * Method invoked when the activity is started.
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (!invokedFromShortcut) {
            mApplication.showProgressDialog(this, R.string.please_wait);
            mApplication.commandsLoad();
            mApplication.historiesLoad();
            reloadAdapter();
            checkForPermissions();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!invokedFromShortcut && mApplication.isMustRestart()) {
            mApplication.setMustRestart(false);
            restartActivity();
        }
    }

    /**
     * Invoked when the activity is put on pause
     */
    @Override
    protected void onPause() {
        mApplication.onClose();
        super.onPause();
    }

    /**
     * Method used to check for application permissions.
     */
    @TargetApi(23)
    private void checkForPermissions() {
        if (mApplication.shouldAskPermissions()) {
            updateOptionsByPermissions();
            if (!mApplication.havePermissionsAsked()) {
                requestForPermissions(mApplication.getAllRequiredPermissions());
            }
        }
    }

    /**
     * Method used to request for application required permissions.
     */
    @TargetApi(23)
    private void requestForPermissions(String[] permissions) {
        if (!Utilities.isEmpty(permissions)) {
            requestPermissions(permissions, PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Prepare main list view with all controls
     */
    private void prepareMainListView() {
        commandsList = (ListView) findViewById(R.id.command_list);
        commandsList.setEmptyView(findViewById(R.id.empty_list_view));
        commandsList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position > -1 && position < adapter.getCount()) {
                    showItemDialogMenu(position);
                }
            }
        });
        adapter = new CommandListAdapter(this, mApplication.getCommands());
        commandsList.setAdapter(adapter);
    }

    /**
     * Reload adapter and commands list.
     */
    public void reloadAdapter() {
        adapter.notifyDataSetChanged();
        commandsList.invalidateViews();
        commandsList.scrollBy(0, 0);
        commandsList.setFastScrollEnabled(mApplication.getCommands().size() > 50);
        mApplication.hideProgressDialog();
    }

    /**
     * This method show the pop up menu when the user do a long click on a list
     * item.
     *
     * @param position The contact position where was made the long click
     */
    private void showItemDialogMenu(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Command command = (Command) adapter.getItem(position);
        builder.setTitle(getString(R.string.item_edit, command.getName()));
        builder.setItems(R.array.menu_list,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                onMenuItemSendSMS(position);
                                break;
                            case 1:
                                onMenuItemDuplicate(position);
                                break;
                            case 2:
                                onMenuItemEdit(position);
                                break;
                            case 3:
                                onMenuItemEditParams(position);
                                break;
                            case 4:
                                onMenuItemAdd();
                                break;
                            case 5:
                                onMenuItemDelete(position);
                                break;
                        }
                    }
                });
        builder.create().show();
    }

    /**
     * This method is invoked when the user chose to edit a command.
     *
     * @param position The position of command to be edited.
     */
    private void onMenuItemEdit(int position) {
        Command command = (Command) adapter.getItem(position);
        new EditorDialog(this, R.string.edit_command, command).show();
    }

    /**
     * This method is invoked when the user chose to edit a command parameters.
     *
     * @param position The position of command parameters to be edited.
     */
    private void onMenuItemEditParams(int position) {
        Command command = (Command) adapter.getItem(position);
        if (command.hasParameters()) {
            mApplication.prepareCommandParameters(command);
            showParameterList(command, METHOD.NOTHING);
        } else {
            showMessageDialog(R.string.information, mApplication.getString(command
                            .havePassword() ? R.string.no_params_to_edit_just_password
                            : R.string.no_params_to_edit, command.getName()),
                    NO_PARAMS_TO_EDIT, command);
        }
    }

    /**
     * Display a list with command parameters to edit them and at the end is
     * called the closedParameterList method.
     *
     * @param command  The command to edit parameters.
     * @param methodId The method id used on the closedParameterList.
     */
    private void showParameterList(final Command command, final METHOD methodId) {
        final Context context = this;
        final ParamListAdapter adapter = new ParamListAdapter(context, command);
        new AlertDialog.Builder(this).setTitle(R.string.param_list_title)
                .setAdapter(adapter, new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String paramName = adapter.getItem(which);
                        ParameterEditor ped = new ParameterEditor(context,
                                command, command
                                .getParameterPosition(paramName));
                        ped.setOnDismissListener(new DialogInterface.OnDismissListener() {

                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                showParameterList(command, methodId);
                            }
                        });
                        ped.show();
                    }

                }).setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                closedParameterList(command, methodId);
            }
        }).create().show();
    }

    /**
     * Method invoked when the parameters list is closed. Based on the method id
     * is invoked another method.
     *
     * @param command  The edited command.
     * @param methodId The method id to be invoked.
     */
    private void closedParameterList(Command command, METHOD methodId) {
        if (command.hasParametersModified()) {
            mApplication.saveCommandParameters(command);
            command.setParametersModified(false);
        }
        if (METHOD.SEND_SMS == methodId) {
            showSendSMSConfirmation(command);
        }
    }

    /**
     * This method is invoked when the user chose to add a new command.
     */
    private void onMenuItemAdd() {
        new EditorDialog(this, R.string.add_command, null).show();
    }

    /**
     * This method is invoked when the user chose to delete a command.
     *
     * @param position The position of command to be deleted.
     */
    private void onMenuItemDelete(int position) {
        final Command command = (Command) adapter.getItem(position);
        if (command != null) {
            showConfirmationDialog(
                    R.string.remove_command,
                    mApplication.getString(R.string.remove_command_question,
                            command.getName()), CONFIRM_ID_DELETE, Integer.valueOf(position));
        }
    }

    /**
     * This method is invoked by the each time when is accepted a confirmation
     * dialog.
     *
     * @param positive       True if the confirmation is positive.
     * @param confirmationId The confirmation ID to identify the case.
     * @param anObject       An object send by the caller method.
     */
    @Override
    protected void onConfirmation(boolean positive, int confirmationId,
                                  Object anObject) {
        if (positive) {
            switch (confirmationId) {
                case CONFIRM_ID_DELETE:
                    doDeleteCommand((Integer) anObject);
                    break;
                case CONFIRM_ID_SMS_SEND:
                    checkSendSMSPermission((Command) anObject);
                    break;
                case CONFIRM_ID_PARAMETERS:
                    showParameterList((Command) anObject, METHOD.SEND_SMS);
                    break;
                case CONFIRM_ID_DONATE:
                    startBrowserWithPage(R.string.donate_url);
                    break;
            }
        } else {
            if (confirmationId == CONFIRM_ID_PARAMETERS) {
                showSendSMSConfirmation((Command) anObject);
            }
        }
    }

    /**
     * Delete a command from the list.
     *
     * @param position The position of command to be deleted.
     */
    private void doDeleteCommand(int position) {
        mApplication.showProgressDialog(this, R.string.please_wait);
        mApplication.getCommands().remove(position);
        mApplication.commandsSave();
        reloadAdapter();
    }

    /**
     * This method is invoked when the user chose to send a command.
     *
     * @param position The position of command to be send.
     */
    private void onMenuItemSendSMS(int position) {
        final Command command = (Command) adapter.getItem(position);
        if (command != null) {
            mApplication.prepareCommandParameters(command);
            if (command.hasParameters()) {
                prepareSMSCommand(command);
            } else {
                showSendSMSConfirmation(command);
            }
        }
    }

    /**
     * Duplicate an existing command.
     *
     * @param position The position of existing command.
     */
    private void onMenuItemDuplicate(int position) {
        Command command = (Command) adapter.getItem(position);
        if (command != null) {
            mApplication.showProgressDialog(this, R.string.please_wait);
            Command copy = (Command) command.clone();
            mApplication.getCommands().add(copy);
            mApplication.commandsSave();
            reloadAdapter();
        }
    }

    /**
     * This method should be used to prepare the SMS command.
     *
     * @param command The SMS command to be prepared.
     */
    private void prepareSMSCommand(Command command) {
        showConfirmationDialog(
                R.string.sms_prepare_title,
                mApplication.getString(R.string.sms_prepare_message,
                        command.getParametersListToBeShow()),
                CONFIRM_ID_PARAMETERS, command);
    }

    /**
     * Before to send the SMS a confirmation dialog is showed to the user to
     * inform about the command.
     *
     * @param command The command to be send to the GPS tracker.
     */
    private void showSendSMSConfirmation(final Command command) {
        int i, size = mApplication.getContacts().size();
        CharSequence[] items = new CharSequence[size];
        boolean[] checkedItems = new boolean[size];
        i = 0;
        for (GpsContact contact : mApplication.getContacts()) {
            items[i] = contact.getName();
            checkedItems[i] = contact.isSelected();
            i++;
        }
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_email)
                .setTitle(
                        mApplication.getString(R.string.send_sms_question,
                                command.getSMSCommandShow()))
                .setMultiChoiceItems(items, checkedItems,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which, boolean isChecked) {
                                GpsContact contact = mApplication.getContacts().get(
                                        which);
                                if (contact != null) {
                                    contact.setSelected(isChecked);
                                }
                            }
                        })
                .setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                onConfirmation(true, CONFIRM_ID_SMS_SEND,
                                        command);
                            }
                        })
                .setNegativeButton(R.string.no,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                onConfirmation(false, CONFIRM_ID_SMS_SEND,
                                        command);
                            }
                        }).show();
    }

    /**
     * Check if the application can send SMS.
     *
     * @param command SMS command to be send.
     */
    private void checkSendSMSPermission(Command command) {
        if (mApplication.hasPermission(TKConfigApplication.PERMISSION_FOR_SEND_SMS)) {
            doSendSMS(command);
        } else {
            showMessageDialog(R.string.information,
                    mApplication.getString(R.string.no_permissions_send_sms), 0,
                    null);
        }
    }

    /**
     * Start the sending process of a SMS with the command to the GPS tracker.
     *
     * @param command The command to be send.
     */
    private void doSendSMS(Command command) {
        String cmd = command.getSMSCommand();
        boolean result = false;
        for (GpsContact contact : mApplication.getContacts()) {
            if (contact.isSelected()) {
                result = true;
                break;
            }
        }
        mApplication.contactsSave();
        if (result) {
            mApplication.sendSMS(this, TKConfigActivity.class, cmd);
        } else {
            showMessageDialog(R.string.information,
                    mApplication.getString(R.string.sms_no_contact), SMS_NO_CONTACT,
                    command);
        }
    }

    /**
     * This method is invoked when is selected a menu item from the option menu
     *
     * @param menuItemId The selected menu item
     */
    @Override
    protected boolean onMenuItemSelected(int menuItemId) {
        boolean processed = false;
        switch (menuItemId) {
            case R.id.menu_add:
                processed = true;
                onMenuItemAdd();
                break;
            case R.id.menu_settings:
                processed = onMenuSettings();
                break;
            case R.id.menu_donate:
                processed = onMenuDonate();
                break;
            case R.id.menu_history:
                processed = onMenuHistory();
                break;
            case R.id.menu_about:
                processed = onMenuAbout();
                break;
            case R.id.menu_exit:
                processed = true;
                onExit();
                break;
        }
        return processed;
    }

    /**
     * Show the about activity
     */
    private boolean onMenuAbout() {
        Intent intent = new Intent(getBaseContext(), AboutActivity.class);
        startActivityForResult(intent, REQUEST_CODE_ABOUT);
        return true;
    }

    /**
     * This is invoked when the user chose the donate item.
     *
     * @return True, because this activity processed the menu item.
     */
    private boolean onMenuDonate() {
        showConfirmationDialog(R.string.donate_title,
                mApplication.getString(R.string.donate_message), CONFIRM_ID_DONATE, null);
        return true;
    }

    /**
     * Show the settings activity (the preference activity)
     *
     * @return True, because this activity processed the menu item.
     */
    private boolean onMenuSettings() {
        Intent intent = new Intent(getBaseContext(), TkPreferences.class);
        startActivityForResult(intent, REQUEST_CODE_SETTINGS);
        return true;
    }

    /**
     * Launch the default browser with a specified URL page.
     *
     * @param urlResourceId The URL resource id.
     */
    private void startBrowserWithPage(int urlResourceId) {
        String url = mApplication.getString(urlResourceId);
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        try {
            startActivity(i);
        } catch (ActivityNotFoundException exception) {
        }
    }

    /**
     * This method is invoked when a child activity is finished and this
     * activity is showed again
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SETTINGS) {
            if (mApplication.isMustReloadCommands()) {
                mApplication.showProgressDialog(this, R.string.please_wait);
                reloadAdapter();
                mApplication.setMustReloadCommands(false);
            }
        }
    }

    /**
     * Launch History Activity
     *
     * @return True, because is processed by this activity.
     */
    private boolean onMenuHistory() {
        Intent intent = new Intent(getBaseContext(), HistoryActivity.class);
        startActivityForResult(intent, 1);
        return true;
    }

    /**
     * Callback for the result from requesting permissions.
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (PERMISSIONS_REQUEST_CODE == requestCode) {
            mApplication.markPermissionsAsked();
            for (String permission : permissions) {
                mApplication.markPermissionAsked(permission);
            }
            updateOptionsByPermissions();
        }
    }

    /**
     * Update settings options based on the allowed permissions.
     */
    private void updateOptionsByPermissions() {
        boolean allowed;
        if (mApplication.shouldAskPermissions()) {
            // functionality
            allowed = mApplication.haveFunctionalPermissions();
        }
    }


    /**
     * Restart this activity.
     */
    private void restartActivity() {
        Intent intent = getIntent();
        finish();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
