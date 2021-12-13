import '../adapter/widget/BryntumWidgetAdapter.js';
import Popup from './Popup.js';

/**
 * @module Core/widget/MessageDialog
 */

/**
 * A singleton modal dialog box which can be used to ask the user to confirm or reject actions.
 *
 * Usage:
 * ```javascript
 *  MessageDialog.confirm({
 *      title: 'Dialog Title',
 *      message : 'Are you sure?'
 *  }).then(result => {
 *      if (result === MessageDialog.yesButton) {
 *          // The YES button was clicked, so go ahead!
 *      }
 *  });```
 *
 * @extends Core/widget/Popup
 * @classType messagedialog
 * @singleton
 */
class MessageDialog extends Popup {
    static get $name() {
        return 'MessageDialog';
    }

    static get defaultConfig() {
        return {
            id : 'bryntum-msgdialog',

            centered : true,

            modal : true,

            hidden : true,

            autoShow : false,

            closeAction : 'hide',

            title : '\xa0',

            items : [{
                type : 'widget',
                id   : 'bryntum-msgdialog-message',
                cls  : 'b-msgdialog-message',
                ref  : 'message'
            }, {
                type : 'textfield',
                id   : 'bryntum-msgdialog-input',
                cls  : 'b-msgdialog-input',
                ref  : 'input'
            }],

            bbar : {
                id    : 'bryntum-msgdialog-bbar',
                items : [{
                    ref     : 'yesButton',
                    id      : 'bryntum-msgdialog-yesbutton',
                    cls     : 'b-msgdialog-yesbutton',
                    text    : 'Yes',
                    onClick : 'up.onYesClick'
                }, {
                    ref     : 'noButton',
                    id      : 'bryntum-msgdialog-nobutton',
                    cls     : 'b-msgdialog-nobutton',
                    text    : 'No',
                    onClick : 'up.onNoClick'
                }, {
                    ref     : 'cancelButton',
                    id      : 'bryntum-msgdialog-cancelbutton',
                    cls     : 'b-msgdialog-cancelbutton',
                    text    : 'Cancel',
                    onClick : 'up.onCancelClick'
                }]
            }
        };
    }

    /**
     * Shows a confirm dialog with "Yes" and "No" buttons. The returned promise resolves passing `true`
     * if the "yes" button is pressed, and `false` if the "No" button is pressed. Typing `ESC` rejects.
     * @param {Object} options An options object for what to show.
     * @param {String} [options.title] The title to show in the dialog header.
     * @param {String} [options.message] The message to show in the dialog body.
     * @async
     */
    async confirm({
        message,
        title = '\xa0'
    }) {
        const me = this;

        me.title = title;
        me.element.classList.remove(me.showClass);

        if (message) {
            me.showClass = 'b-show-message-yes-no';
            me.widgetMap.message.html = message;
        }
        else {
            me.showClass = 'b-show-yes-no';
        }
        me.element.classList.add(me.showClass);

        me.show();
        return me.promise = new Promise((resolve) => {
            me.resolve = resolve;
        });
    }

    doResolve(value) {
        const
            me          = this,
            { resolve } = me;

        if (resolve) {
            me.resolve = me.reject = me.promise = null;
            resolve(value);
            me.hide();
        }
    }

    onInternalKeyDown(event) {
        // Cancel on escape key
        if (event.key === 'Escape') {
            event.stopImmediatePropagation();
            if (this.widgetMap.cancelButton.isVisible) {
                this.onCancelClick();
            }
            else {
                this.onNoClick();
            }
        }
        super.onInternalKeyDown(event);
    }

    onYesClick() {
        this.doResolve(md.yesButton);
    }

    onNoClick() {
        this.doResolve(md.noButton);
    }

    onCancelClick() {
        this.doResolve(md.cancelButton);
    }
}

const md = new MessageDialog();

md.noButton = 0;
md.yesButton = 1;
md.cancelButton = 3;

export { md as default };
