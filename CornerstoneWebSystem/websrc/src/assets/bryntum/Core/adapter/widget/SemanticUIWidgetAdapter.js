/* globals DateColumn: true */
//TODO: Handle listeners in addition to onChange, onIcon etc?
//TODO: Document what functions, configs each widget must have

import DateHelper from '../../helper/DateHelper.js';
import DomHelper from '../../helper/DomHelper.js';
import WidgetHelper from '../../helper/WidgetHelper.js';

// OLD IMPLEMENTATION, NOT TO BE USED

export default class SemanticUIWidgetAdapter {
    //region Widgets

    createSlider(config) {
        let ui = document.createElement('div');
        ui.className = 'ui slider';

        let slider = document.createElement('input');
        slider.setAttribute('type', 'range');

        ui.appendChild(slider);

        if (config.id) ui.id = config.id;
        if ('max' in config) slider.max = config.max;
        if ('min' in config) slider.min = config.min;
        if ('step' in config) slider.step = config.step;

        let label;
        if (config.label) {
            label = document.createElement('div');
            label.className = 'label';
            label.innerHTML = config.label;
            ui.appendChild(label);
        }

        function updateLabel(value) {
            if (config.showValue) {
                label.innerHTML = config.label + ` (${value})`;
            }
        }

        let widget = {
            input   : slider,
            element : ui,
            set value(value) {
                slider.value = value;
                updateLabel(value);
            },
            get value() {
                return parseInt(slider.value);
            },
            set max(value) {
                // trigger change if value is adjusted
                if (widget.value > value) {
                    widget.value = value;
                    if (config.onChange) widget.triggerChange();
                }
                slider.max = value;
            },
            get max() {
                return parseInt(slider.max);
            },
            set label(label) {
                label && (label.innerHTML = label);
            },
            triggerChange() {
                config.onChange(widget, parseInt(slider.value));
                updateLabel(slider.value);
            }
        };

        if ('value' in config) widget.value = config.value;

        if (config.onChange) {
            slider.addEventListener('change', () => widget.triggerChange());
        }

        return widget;
    }

    createCheckboxWidget(config) {
        let style = config.style || 'toggle';
        if (style === 'normal') style = '';

        let ui = document.createElement('div');
        ui.className = `ui ${style} checkbox`;
        if (config.id) ui.id = config.id;

        let input = document.createElement('input');
        input.type = 'checkbox';
        if (config.id) input.id = 'input_' + config.id;
        ui.appendChild(input);

        let label = document.createElement('label');
        ui.appendChild(label);

        if (config.label) {
            label.innerHTML = config.label;
            if (config.id) label.setAttribute('for', 'input_' + config.id);
        }
        else {
            ui.className += ' fitted';
        }

        if (config.checked) input.checked = true;

        let widget = {
            element : ui,
            input   : input
        };

        if (config.onChange) {
            input.addEventListener('click', event => {
                config.onChange(widget, input.checked, event);
            });
        }

        return widget;
    }

    createTextWidget(config, inputType = 'text') {
        let uiInput = document.createElement('div');
        uiInput.className = 'ui input';

        let input = document.createElement('input');
        input.type = inputType;
        if (config.placeholder) input.placeholder = config.placeholder;

        uiInput.appendChild(input);

        let badge;
        if (config.badge) {
            badge = document.createElement('div');
            badge.className = 'floating ui yellow tiny circular label';
            if (typeof config.badge !== 'boolean') {
                badge.innerHTML = config.badge;
            }
            else {
                badge.classList.add('hidden');
            }
            uiInput.appendChild(badge);
        }

        if (config.tooltip) {
            $(input).popup({
                html : config.tooltip,
                on   : 'focus'
            });
        }

        if (config.label) {
            uiInput.className += ' labeled';
            let label = document.createElement('div');
            label.className = 'ui label';
            label.innerHTML = config.label;
            uiInput.insertBefore(label, input);
        }

        if (config.clearable) {
            config.icon = 'remove';
            config.onIcon = () => {
                input.value = '';
                if (config.onClear) config.onClear.call(widget, widget);
            };
        }

        if (config.icon) {
            uiInput.className += ' ' + (config.iconPosition || 'right') + ' icon';
            let icon = document.createElement('i');
            icon.className = 'link icon ' + config.icon;
            uiInput.appendChild(icon);

            if (config.onIcon) {
                icon.onclick = function() {
                    config.onIcon.call(widget, widget);
                };
            }
        }

        if (config.iconLabel) {
            uiInput.className += ' labeled';
            let label = document.createElement('div');
            label.className = 'ui label';
            label.innerHTML = `<i class="${config.iconLabel} icon"></i>`;
            uiInput.insertBefore(label, input);
        }

        if (config.value) {
            input.value = config.value;
        }

        let widget = {
            element  : uiInput,
            input    : input,
            focus    : () => input.focus(),
            select   : () => input.select(),
            setValue : text => input.value = text,
            getValue : () => input.value,
            setBadge : text => {
                if (badge) {
                    badge.innerHTML = text;
                    badge.classList[!text || text.length === 0 ? 'add' : 'remove']('hidden');
                    // when IE11 support is dropped
                    // badge.classList.toggle('hidden', !text || text.length === 0);
                }
            }
        };

        if (config.onChange) {
            uiInput.onchange = function() {
                config.onChange.call(widget, widget, input.value); // TODO: thisObj
            };
        }

        if (config.onEsc) {
            uiInput.onkeydown = function(event) {
                if (event.key === 'Escape') config.onEsc.call(widget);
            };
        }

        return widget;
    }

    createDateWidget(config) {
        let uiCalendar = document.createElement('div');
        uiCalendar.className = 'ui calendar';

        let uiInput = document.createElement('div');
        uiInput.className = 'ui input left icon';
        uiCalendar.appendChild(uiInput);

        let icon = document.createElement('i');
        icon.className = 'calendar icon';
        uiInput.appendChild(icon);

        let input = document.createElement('input');
        input.type = 'text';
        if (config.placeholder) input.placeholder = config.placeholder;
        uiInput.appendChild(input);

        /*if (config.label) {
         uiInput.className += ' labeled';
         let label = document.createElement('div');
         label.className = 'ui label';
         label.innerHTML = config.label;
         uiInput.insertBefore(label, input);
         }*/

        let initialSet = false;

        $(uiCalendar).calendar({
            type           : 'date',
            firstDayOfWeek : 1,
            today          : true,
            on             : 'click',
            formatter      : {
                date(date, settings) {
                    return DateHelper.format(date, config.format || DateColumn.Defaults.format);
                }
            },
            onChange : function(date, text) {
                if (!initialSet) config.onChange && config.onChange.call(widget, widget, date);
                return true;
            }
        });

        if (config.value) {
            initialSet = true;
            $(uiCalendar).calendar('set date', config.value);
        }

        let widget = {
            element  : uiCalendar,
            input    : input,
            focus    : () => $(uiCalendar).calendar('focus'), //input.focus(),
            setValue : date => $(uiCalendar).calendar('set date', date),
            getValue : () => $(uiCalendar).calendar('get date') //input.value
        };

        return widget;
    }

    createButtonWidget(config) {
        let buttonConfig = {
                tag       : 'button',
                className : 'ui button'
            },
            html         = '';

        if (config.id) buttonConfig.id = config.id;
        if (config.ui) buttonConfig.className += ' ' + config.ui;
        if (config.color) buttonConfig.className += ' ' + config.color;
        if (config.style) buttonConfig.className += ' ' + config.style;
        if (config.icon) {
            buttonConfig.className += ' icon';
            html += `<i class="${config.icon} icon"></i>`;
        }
        if (config.text) html += config.text;
        if (config.disabled) buttonConfig.className += ' disabled';

        buttonConfig.html = html;

        let buttonEl = DomHelper.createElement(buttonConfig);

        let badge = null;
        if (config.badge) {
            badge = document.createElement('div');
            badge.className = 'floating ui red tiny circular label';
            if (typeof config.badge !== 'boolean') {
                badge.innerHTML = config.badge;
            }
            else {
                badge.classList.add('hidden');
            }
            buttonEl.appendChild(badge);
        }

        if (config.onClick) buttonEl.onclick = config.onClick;

        if (config.tooltip) {
            let tooltipConfig = config.tooltip;
            if (typeof config.tooltip === 'string') {
                tooltipConfig = {
                    content  : config.tooltip,
                    position : 'bottom center'
                };
            }
            $(buttonEl).popup(tooltipConfig);
        }

        let widget = {
            element : buttonEl,
            enable  : () => {
                buttonEl.classList.remove('disabled');
            },
            disable : () => {
                buttonEl.classList.add('disabled');
            },
            setBadge : text => {
                if (badge) {
                    badge.innerHTML = text;
                    badge.classList[!text || text.length === 0 ? 'add' : 'remove']('hidden');
                    // when IE11 support is dropped
                    // badge.classList.toggle('hidden', !text || text.length === 0);
                }
            }
        };

        if (config.onAction) {
            buttonEl.addEventListener('click', (event) => {
                config.onAction(widget, event);
            });
        }

        return widget;
    }

    createDropDown(config) {
        let elementConfig = {
                tag       : 'select',
                className : 'ui dropdown'
            },
            html          = '';

        if (config.placeHolder || config.placeholder) html += `<option value="">${config.placeholder}</option>`;

        // Map from [[val, text], ...] or [val,val] to [{value: , text: }]
        config.items = config.items.map(
            item => Array.isArray(item)
                // Item is array, map [value, text] -> { value: value, text: text }
                ? { value : item[0], text : item[1] }
                // Item is string, map str -> { value: str, text: str }
                : typeof item === 'string'
                    ? { value : item, text : item }
                    // Item is really an item, use it
                    : item
        );

        config.items.forEach(item => {
            html += `<option value="${item.value}" ${item.selected ? 'selected' : ''}>${item.text}</option>`;
        });

        elementConfig.html = html;

        let element = DomHelper.createElement(elementConfig);

        return {
            element  : element,
            focus    : () => $(element).dropdown('show'),
            setValue : value => $(element).dropdown('set selected', value),
            getValue : () => $(element).dropdown('get value')[0],
            init     : () => {
                let initConfig = {};
                if (config.onAction) initConfig.onChange = config.onAction;
                $(element).dropdown(initConfig);
            }
        };
    }

    //endregion

    // region Create, insert, append
    createWidget(config) {
        config = Object.assign({}, config);

        switch (config.type.toLowerCase()) {
            case 'check':
            case 'checkbox':
                return this.createCheckboxWidget(config);

            case 'text':
                return this.createTextWidget(config, 'text');

            case 'number':
                return this.createTextWidget(config, 'number');

            case 'date':
                return this.createDateWidget(config);

            case 'button':
                return this.createButtonWidget(config);

            case 'dropdown':
                return this.createDropDown(config);

            case 'slider':
                return this.createSlider(config);
        }
    }

    appendWidget(containerEl, config) {
        const widget = WidgetHelper.createWidget(config);
        containerEl.appendChild(widget.element);
        return widget;
    }

    appendWidgets(containerEl, configArray) {
        return configArray.map(item => this.appendWidget(containerEl, item));
    }

    insertWidget(containerEl, config) {
        const widget = WidgetHelper.createWidget(config);
        DomHelper.insertFirst(containerEl, widget.element);
        return widget;
    }

    insertWidgets(containerEl, configArray) {
        return configArray.reverse().map(item => this.insertWidget(containerEl, item)).reverse();
    }

    //endregion

    //region Window & popup
    openWindow(config) {
        var winEl = document.createElement('div');
        winEl.className = 'ui modal';
        winEl.innerHTML = `
            <i class="close icon"></i>
            <div class="header">
                ${config.title}
            </div>
            <div class="content">
                <p>Some text and stuff</p>
            </div>
            <div class="actions">
                <div class="ui black deny button">Cancel</div>
                <div class="ui positive right labeled icon button">
                    OK
                    <i class="checkmark icon"></i>
                </div>
            </div>
        `;
        document.body.appendChild(winEl);
        $(winEl).modal('show');
    }

    openPopup(element, config) {
        let popupContent = DomHelper.createElement({
                tag : 'div'
            }),
            widgets      = [];

        if (typeof config === 'string') {
            popupContent.innerHTML = config;
            config = {};
        }

        if (config.items) {
            widgets = WidgetHelper.append(config.items, popupContent);
        }

        if (config.html) {
            popupContent.innerHTML = config.html;
        }

        if (config.cls) {
            popupContent.classList.add(config.cls);
        }

        $(element).popup({
            exclusive : true,
            position  : config.position || 'top center',
            html      : popupContent,
            on        : config.on || 'manual',
            onVisible : () => {
                if (widgets.length > 0) widgets[0].focus();
            }
        });

        if (!config.on || config.on === 'manual') $(element).popup('show');

        return {
            show    : () => $(element).popup('show'),
            close   : () => $(element).popup('hide'),
            widgets : widgets
        };
    }

    //endregion

    //region Menu

    addMenuItems(items, flatItems, onItem) {
        let html = '';

        items.forEach(item => {
            // subitems inherit onItem from parent, but can specify own
            if (onItem && !item.onItem) item.onItem = onItem;

            if (item.items) {
                html += `
                <div class="ui dropdown item">
                    <i class="dropdown icon"></i>
                    ${item.text}
                    <div class="right menu">
                    ${this.addMenuItems(item.items, flatItems, item.onItem)}
                    </div>
                </div>`;
            }
            else {
                html += `
                <a class="item ${item.cls || ''}" id="${item.id}" data-index="${flatItems.length}" data-data="${item.data}">
                    ${item.icon ? `<i class="${item.icon} icon"></i>` : ''}
                    ${item.text}
                </a>`;
                flatItems.push(item);
            }
        });

        return html;
    }

    showContextMenu(element, config) {
        let itemHtml  = '',
            flatItems = [];

        if (config.items) itemHtml += this.addMenuItems(config.items, flatItems);

        let popupContent = DomHelper.createElement({
            tag  : 'div',
            html : `<div class="ui vertical menu">${itemHtml}</div>`
        });

        $(element).popup({
            exclusive : true,
            position  : config.position || 'bottom center',
            html      : popupContent,
            variation : 'context-menu',
            on        : 'manual',
            onVisible : function(el) {
                let popup = this[0];
                if (config.onItem) {
                    popup.addEventListener('click', (event) => {
                        //event.preventDefault();
                        event.stopPropagation();
                        let index = event.target.dataset.index;
                        if (index) {
                            $(element).popup('hide');
                            config.onItem(flatItems[parseInt(index)]);
                        }
                    });
                }
            }
        });

        $(element).popup('show');

        // activate sub menus
        $('.context-menu .ui.dropdown').dropdown({
            onChange : function(value, text, selected) {
                let index = selected[0].dataset.index;
                if (index) {
                    let item = flatItems[parseInt(index)];
                    if (config.onItem) config.onItem(item);
                    if (item.onItem) item.onItem(item);
                }
            }
        });

        return {
            close : () => $(element).popup('hide')
        };
    }

    //endregion

    //region Tooltip

    attachTooltip(element, configOrText) {
        if (typeof configOrText === 'string') configOrText = { text : configOrText };

        $(element).popup({
            content  : configOrText.text,
            position : configOrText.position || 'top center',
            on       : configOrText.on
        });
        return element;
    }

    hasTooltipAttached(element) {
        return $(element).popup('exists');
    }

    destroyTooltipAttached(element) {
        return $(element).popup('destroy');
    }
    //endregion

    //region Mask
    mask(element, msg = 'Loading') {
        let dimmer = DomHelper.getChild(element, '.dimmer');
        if (!dimmer) {
            dimmer = DomHelper.createElement({
                tag       : 'div',
                className : 'ui inverted dimmer',
                html      : `<div class="ui text loader">${msg}</div>`
            });
            element.appendChild(dimmer);
        }
        $(dimmer).dimmer('show');
    }

    unmask(element) {
        let dimmer = DomHelper.getChild(element, '.dimmer');
        if (dimmer) $(dimmer).dimmer('hide');
    }

    //endregion
}
