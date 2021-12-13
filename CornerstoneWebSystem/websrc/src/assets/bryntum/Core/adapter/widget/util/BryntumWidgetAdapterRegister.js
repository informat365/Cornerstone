/**
 * Keeps a map of registered widgets. Used by BryntumWidgetAdapter to create widgets using `{ type : 'xx' }`.
 * @private
 */
export default class BryntumWidgetAdapterRegister {
    static register(type, widgetClass) {
        const classEntry = widgetClassRegister[widgetClass];

        type = type.toLowerCase();
        
        // Create a mapping from the type to the class
        widgetRegister[type] = widgetClass;

        // Create a mapping from the class to its type identifier(s)
        if (classEntry) {
            classEntry[type] = 1;
        }
        else {
            widgetClassRegister[widgetClass] = {
                [type] : 1
            };
        }
    }

    static getClass(type) {
        return widgetRegister[type.toLowerCase()];
    }

    /**
     * Determines whether the passed widget matches the passed type. So a {@link Core.widget.Combo}
     * would match any of its three registered types, `'combobox'`, '`combo'` or `'dropdown'`.
     *
     * Pass the `deep` parameter as `true` to test the class hierarchy also, so if `deep`, a
     * {@link Core.widget.Combo} would also match `'pickerfield'`, `'field'` and `'widget'`.
     *
     * Note that this is *type* matching, not querying, so patterns such as `'*'` will not match.
     * @param {Core.widget.Widget} widget The widget to test.
     * @param {String} type The stype string to test against.
     * @param {Boolean} [deep] Whether to test the widget's superclass hierarchy.
     * @returns {Boolean} `true` if the passed widget matches the passed selector.
     */
    static isType(widget, type, deep) {
        type = type.toLowerCase();
        
        for (let widgetClass = widget.constructor; widgetClass; widgetClass = deep && Object.getPrototypeOf(widgetClass)) {
            // Find a registered entry.
            // If it's an abstract based class which was not registered (such as PickerField),
            // create an entry for it using its lowercased $name
            const
                // Built in widgets all define $name to be safer from obfustaction, but custom widgets might not
                name = widgetClass.hasOwnProperty('$name') ? widgetClass.$name : widgetClass.name,
                classEntry = widgetClassRegister[widgetClass] || (name && (widgetClassRegister[widgetClass] = {
                    [name.toLowerCase()] : 1
                }));

            if (classEntry && classEntry[type]) {
                return true;
            }
        }
    }

    static createWidget(type, config) {
        const widgetClass = this.getClass(type || 'widget');

        if (widgetClass) {
            return new widgetClass(config);
        }

        //<debug>
        throw new Error(`Invalid widget type ${type} specified`);
        //</debug>

        // eslint-disable-next-line no-unreachable
        return null;
    }
}

const
    widgetRegister = BryntumWidgetAdapterRegister.widgetRegister = {},
    widgetClassRegister = BryntumWidgetAdapterRegister.widgetClassRegister = {};
