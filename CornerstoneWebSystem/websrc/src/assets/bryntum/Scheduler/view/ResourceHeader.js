import Widget from '../../Core/widget/Widget.js';
import DomHelper from '../../Core/helper/DomHelper.js';
import DomSync from '../../Core/helper/DomSync.js';
import EventHelper from '../../Core/helper/EventHelper.js';
import DomClassList from '../../Core/helper/util/DomClassList.js';
import StringHelper from '../../Core/helper/StringHelper.js';

/**
 * @module Scheduler/view/ResourceHeader
 */

/**
 * Header widget that renders resource column headers and acts as the interaction point for resource columns in vertical
 * mode. Note that it uses virtual rendering and element reusage to gain performance, only headers in view are available
 * in DOM. Because of this you should avoid direct element manipulation, any such changes can be discarded at any time.
 *
 * By default it displays resources `name` and also applies its `iconCls` if any, like this:
 *
 * ```
 * <i class="iconCls">name</i>
 * ```
 *
 * If Scheduler is configured with a {@link Scheduler.view.mixin.SchedulerEventRendering#config-resourceImagePath} the
 * header will render miniatures for the resources, using {@link Scheduler.model.ResourceModel#field-imageUrl} with
 * fallback to {@link Scheduler.model.ResourceModel#field-name} + .jpg for unset values.
 *
 * The contents and styling of the resource cells in the header can be customized using {@link #config-headerRenderer}:
 *
 * ```
 * new Scheduler({
 *     mode            : 'vertical',
 *     resourceColumns : {
 *         headerRenderer : ({ resourceRecord }) => `Hello ${resourceRecord.name}`
 *     }
 * }
 *```
 *
 * The width of the resource columns is determined by the {@link #config-columnWidth} config.
 *
 * @extends Core/widget/Widget
 */
export default class ResourceHeader extends Widget {

    //region Config

    static get $name() {
        return 'ResourceHeader';
    }

    static get defaultConfig() {
        return {
            /**
             * Resource store used to render resource headers. Assigned from Scheduler.
             * @config {Scheduler.data.ResourceStore}
             * @private
             */
            resourceStore : null,

            // TODO: Read this value from CSS as we do with rowHeight?
            /**
             * Width for each resource column
             * @config {Number}
             */
            columnWidth : 150,

            /**
             * Custom header renderer function. Can be used to manipulate the element config used to create the element
             * for the header:
             *
             * ```
             * new Scheduler({
             *   resourceColumns : {
             *     headerRenderer({ elementConfig, resourceRecord }) {
             *       elementConfig.dataset.myExtraData = 'extra';
             *       elementConfig.style.fontWeight = 'bold';
             *     }
             *   }
             * });
             * ```
             *
             * See {@link Core.helper.DomHelper#function-createElement-static DomHelper#createElement()} for more information.
             * Please take care to not break the default configs :)
             *
             * Or as a template by returning HTML from the function:
             *
             * ```
             * new Scheduler({
             *   resourceColumns : {
             *     headerRenderer : ({ resourceRecord }) => `
             *       <div class="my-custom-template">
             *       ${resourceRecord.firstName} {resourceRecord.surname}
             *       </div>
             *     `
             *   }
             * });
             * ```
             *
             * NOTE: When using `headerRenderer` no default internal markup is applied to the resource header cell,
             * `iconCls` and `imageUrl` will have no effect unless you supply custom markup for them.
             *
             * @config {Function}
             * @param {Object} params Object containing the params below
             * @param {Scheduler.model.ResourceModel} resourceRecord Resource whose header is being rendered
             * @param {Object} elementConfig A {@link Core.helper.DomHelper#function-createElement-static} config object used to create the element for the resource
             */
            headerRenderer : null,

            /**
             * Automatically resize resource columns to **fill** available width. Set to `false` to always respect the
             * configured `columnWidth`
             * @config {Boolean}
             * @default
             */
            fillWidth : true,

            /**
             * Automatically resize resource columns to always **fit** available width
             * @config {Boolean}
             * @default
             */
            fitWidth : false,

            // Copied from Scheduler#resourceImagePath on creation in TimeAxisColumn.js
            imagePath : null,

            // Copied from Scheduler#resourceImagePath on creation in TimeAxisColumn.js
            defaultImageName : null
        };
    }

    static get properties() {
        return {
            firstResource : -1,
            lastResource  : -1
        };
    }

    //endregion

    //region Init

    construct(config) {
        super.construct(config);

        const me = this;

        if (me.imagePath != null) {
            // Apply default image on load errors
            EventHelper.on({
                element  : me.element,
                delegate : '.b-resource-image',
                capture  : true,
                error    : event => this.setDefaultResourceImage(event.target)
            });

            // Need to gain some height when displaying images
            me.element.classList.add('b-has-images');
        }
    }

    //endregion

    //region ResourceStore

    set resourceStore(store) {
        const me = this;

        if (store !== me._resourceStore) {
            me.resourceStoreDetacher && me.resourceStoreDetacher();

            me._resourceStore = store;

            me.resourceStoreDetacher = store.on({
                change  : 'onResourceStoreDataChange',
                thisObj : me
            });

            // Already have data? Update width etc
            if (store.count) {
                me.onResourceStoreDataChange({});
            }
        }
    }

    get resourceStore() {
        return this._resourceStore;
    }

    // Redraw resource headers on any data change
    onResourceStoreDataChange({ action }) {
        const
            me = this,
            width = me.resourceStore.count * me.columnWidth;

        if (width !== me.width) {
            me.element.style.width = width + 'px';
            // During setup, silently set the width. It will then render correctly. After setup, let the world know...
            me.column.set('width', width, me.column.grid.isConfiguring);
        }

        if (action === 'removeall') {
            // Keep nothing
            me.element.innerHTML = '';
        }

        if (action === 'remove' || action === 'add' || action === 'filter') {
            me.refreshWidths();
        }
    }

    //endregion

    //region Properties

    get columnWidth() {
        return this._columnWidth;
    }

    set columnWidth(width) {
        const me = this;

        if (width !== me._columnWidth) {
            const oldWidth  = me._columnWidth;
            me._columnWidth = width;

            // Flag set in refreshWidths, do not want to create a loop
            if (!me.refreshingWidths) {
                me._originalColumnWidth = width;

                me.refreshWidths();
            }

            if (!me.isConfiguring) {
                me.refresh();
                // Cannot trigger with requested width, might have changed because of fit/fill
                me.trigger('columnWidthChange', { width : me._columnWidth, oldWidth });
            }
        }
    }

    /**
     * Assign to toggle resource columns **fill* mode. `true` means they will stretch (grow) to fill viewport, `false`
     * that they will respect their configured `columnWidth`.
     * @property {Boolean}
     */
    get fillWidth() {
        return this._fillWidth;
    }

    set fillWidth(fill) {
        this._fillWidth = fill;

        this.refreshWidths();
    }

    /**
     * Assign to toggle resource columns **fit* mode. `true` means they will grow or shrink to always fit viewport,
     * `false` that they will respect their configured `columnWidth`.
     * @property {Boolean}
     */
    get fitWidth() {
        return this._fitWidth;
    }

    set fitWidth(fit) {
        this._fitWidth = fit;

        this.refreshWidths();
    }

    setDefaultResourceImage(target) {
        const defaultURL = this.getImageURL(this.defaultImageName);
        // Set image to defaultURL if it is not already set
        if (target.src && !target.src.endsWith(defaultURL.replace(/\.\.\//gm, ''))) {
            target.src = defaultURL;
        }
    }

    getImageURL(imageName) {
        return StringHelper.joinPaths([this.imagePath || '', imageName || '']);
    }

    get imagePath() {
        return this._imagePath;
    }

    set imagePath(path) {
        this._imagePath = path;

        this.refresh();
    }

    //endregion

    //region Fit to width

    get availableWidth() {
        return this._availableWidth;
    }

    set availableWidth(width) {
        this._availableWidth = width;

        this.refreshWidths();
    }

    // Updates the column widths according to fill and fit settings
    refreshWidths() {
        const
            me                        = this,
            {
                availableWidth,
                _originalColumnWidth
            }                         = me,
            count                     = me.resourceStore && me.resourceStore.count;

        // Bail out if availableWidth not yet set or resource store not assigned/loaded
        if (!availableWidth || !count) {
            return;
        }

        me.refreshingWidths = true;

        const
            // Fit width if configured to do so or if configured to fill and used width is less than available width
            fit = me.fitWidth || me.fillWidth && _originalColumnWidth * count < availableWidth,
            useWidth = fit ? Math.floor(availableWidth / count) : _originalColumnWidth,
            shouldAnimate =  me.column.grid.enableEventAnimations && Math.abs(me._columnWidth - useWidth) > 30;

        DomHelper.addTemporaryClass(me.element, 'b-animating', shouldAnimate ? 300 : 0);

        me.columnWidth = useWidth;

        me.refreshingWidths = false;
    }

    //endregion

    //region Rendering

    // Visual resource range, set by VerticalEventMapper
    set range(range) {
        const me = this;

        me.firstResource = range.firstResource;
        me.lastResource = range.lastResource;

        me.refresh();
    }

    /**
     * Refreshes the visible headers
     */
    refresh() {
        const
            me = this,
            { firstResource, lastResource } = me;

        // Bail out if we are configuring or have no resources to show
        if (me.column.grid.isConfiguring || firstResource === -1 || lastResource === -1 || lastResource >= me.resourceStore.count) {
            return;
        }

        const configs = [];

        // Gather element configs for resource headers in view
        for (let i = firstResource; i <= lastResource; i++) {
            const
                resourceRecord = me.resourceStore.getAt(i),
                elementConfig = {
                    // Might look like overkill to use DomClassList here, but can be used in headerRenderer
                    className : new DomClassList({
                        'b-resourceheader-cell' : 1
                    }),
                    dataset : {
                        resourceId : resourceRecord.id
                    },
                    style : {
                        left  : i * me.columnWidth,
                        width : me.columnWidth
                    },
                    children : []
                };

            // Let a configured headerRenderer have a go at it before applying
            if (me.headerRenderer) {
                const value = me.headerRenderer({ elementConfig, resourceRecord });
                if (value != null) {
                    elementConfig.html = value;
                }
            }
            // No headerRenderer, apply default markup
            else {
                // Optionally displaying a miniature image
                if (me.imagePath != null) {
                    const imgName = resourceRecord.name ? resourceRecord.imageUrl || (resourceRecord.name.toLowerCase() + '.jpg') : me.defaultImageName;

                    elementConfig.children.push({
                        tag       : 'img',
                        className : 'b-resource-image',
                        src       : me.getImageURL(imgName)
                    });
                }

                // Optionally displaying an icon
                if (resourceRecord.iconCls) {
                    elementConfig.children.push({
                        tag       : 'i',
                        className : resourceRecord.iconCls
                    });
                }

                // By default showing resource name
                elementConfig.children.push({
                    tag       : 'span',
                    className : 'b-resource-name',
                    html      : resourceRecord.name
                });
            }

            configs.push(elementConfig);
        }

        // Sync changes to the header
        DomSync.sync({
            domConfig : {
                onlyChildren : true,
                children     : configs
            },
            targetElement : me.element,
            syncIdField   : 'resourceId'
            // TODO: Add callback here to trigger events when rendering/derendering header cells. Sooner or later
            //  someone is going to ask for a way to render JSX or what not to the header
        });
    }

    //endregion
}
