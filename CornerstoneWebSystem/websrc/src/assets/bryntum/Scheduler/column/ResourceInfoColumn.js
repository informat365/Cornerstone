import Column from '../../Grid/column/Column.js';
import ColumnStore from '../../Grid/data/ColumnStore.js';
import EventHelper from '../../Core/helper/EventHelper.js';
import StringHelper from '../../Core/helper/StringHelper.js';

/**
 * @module Scheduler/column/ResourceInfoColumn
 */

/**
 * Displays resource information. Defaults to showing image + name + event count, but what to show is configurable.
 * Be sure to specify {@link Scheduler.view.mixin.SchedulerEventRendering#config-resourceImagePath} to instruct the
 * column where to look for the images.
 * @externalexample scheduler/ResourceInfoColumn.js
 * @classType resourceInfo
 * @extends Grid/column/Column
 */
export default class ResourceInfoColumn extends Column {
    static get type() {
        return 'resourceInfo';
    }

    static get fields() {
        return ['showEventCount', 'showRole', 'showImage', 'imagePath', 'validNames', 'defaultImageName', 'autoScaleThreshold'];
    }

    static get defaults() {
        return {
            /**
             * Show image. Looks for image name in fields on the resource in the following order: 'imageUrl', 'image', 'name'.
             * Set `showImage` to a field name to use a custom field. Set `imagePath` to specify where to load
             * images from. If no extension found, defaults to .jpg.
             * @config {Boolean}
             * @default
             */
            showImage : true,

            /**
             * Show number of events assigned to the resource below the name.
             * @config {Boolean}
             * @default
             */
            showEventCount : true,

            /**
             * Show resource role below the name. Specify `true` to display data from the `role` field, or specify a field
             * name to read this value from.
             * @config {Boolean|String}
             * @default
             */
            showRole : false,

            /**
             * Path to load images from. Defaults to using the value of {@link Scheduler.view.mixin.SchedulerEventRendering#config-resourceImagePath}
             * @config {String}
             * @deprecated 2.2
             */
            imagePath : null,

            /**
             * Valid image names. Set to `null` to allow all names.
             * @config {String[]}
             * @default
             */
            validNames : [
                'amit',
                'angelo',
                'arcady',
                'arnold',
                'celia',
                'chang',
                'dan',
                'dave',
                'emilia',
                'george',
                'gloria',
                'henrik',
                'hitomi',
                'jong',
                'kate',
                'lee',
                'linda',
                'lisa',
                'lola',
                'macy',
                'madison',
                'malik',
                'mark',
                'maxim',
                'mike',
                'rob',
                'steve'
            ],

            /**
             * Generic user image, used when an invalid name is specified
             * @config {String}
             * @deprecated 2.2
             */
            defaultImageName : null,

            /**
             * Specify 0 to prevent the column from adapting its content according to the used row height, or specify a
             * a threshold (row height) at which scaling should start.
             * @config {Number}
             * @default
             */
            autoScaleThreshold : 40,

            field      : 'name',
            htmlEncode : false,
            width      : 140,

            autoSyncHtml : true
        };
    }

    construct() {
        const me = this;

        super.construct(...arguments);

        if (me.grid.isPainted) {
            me.addErrorListener();
        }
        else {
            me.grid.on({
                paint   : me.addErrorListener,
                thisObj : me,
                once    : true
            });
        }
    }

    set imagePath(path) {
        this.set('imagePath', path);
    }

    get imagePath() {
        return this.get('imagePath') || this.grid.resourceImagePath;
    }

    set defaultImageName(name) {
        this.set('defaultImageName', name);
    }

    get defaultImageName() {
        return this.get('defaultImageName') || this.grid.defaultResourceImageName;
    }

    getImageURL(imageName) {
        return StringHelper.joinPaths([this.imagePath || '', imageName || '']);
    }

    addErrorListener() {
        EventHelper.on({
            element  : this.grid.element,
            delegate : '.b-resource-image',
            error    : event => this.setDefaultResourceImage(event.target),
            capture  : true
        });
    }

    setDefaultResourceImage(target) {
        const defaultURL = (this.defaultImageName);
        // Set image to defaultURL if it is not already set
        if (target.src && !target.src.endsWith(defaultURL.replace(/\.\.\//gm, ''))) {
            target.src = defaultURL;
        }
    }

    template(record) {
        const me        = this,
            {
                showImage,
                showRole,
                showEventCount
            }         = me,
            roleField = typeof showRole === 'string' ? showRole : 'role',
            role      = record[roleField],
            count     = record.events.length,
            value     = record.get(me.field);

        let imageUrl;

        if (showImage) {
            if (record.imageUrl) {
                imageUrl = record.imageUrl;
            }
            else {
                // record.image supposed to be a file name, located at imagePath
                let imageName = typeof showImage === 'string' ? showImage : (record.image || (value && value.toLowerCase()) || me.defaultImageName);

                imageUrl = me.getImageURL(imageName);

                // Image name supposed to have an extension
                if (!imageName.includes('.')) {
                    // If validNames is specified, check that imageName is valid
                    if (!me.validNames || me.validNames.includes(imageName)) {
                        imageUrl += '.jpg';
                    }
                    // If name is not valid, use generic image
                    else {
                        imageUrl = (me.defaultImageName);
                    }
                }
            }
        }

        return `
            <div class="b-resource-info">
                ${imageUrl ? `<img class="b-resource-image" draggable="false" src="${imageUrl}">` : ''}
                <dl>
                <dt>${value}</dt>
                ${showRole ? `<dd class="b-resource-role">${role}</dd>` : ''}
                ${showEventCount ? `<dd class="b-resource-events">${count+'条数据'}</dd>` : ''}
                </dl>
            </div>
        `;
    }

    defaultRenderer({ grid, record, cellElement, value, isExport }) {
        let result;

        if (record.meta.specialRow) {
            result = '';
        }
        else if (isExport) {
            result = value;
        }
        else {
            if (this.autoScaleThreshold && grid.rowHeight < this.autoScaleThreshold) {
                cellElement.style.fontSize = (grid.rowHeight / 40) + 'em';
            }
            else {
                cellElement.style.fontSize = '';
            }

            result = this.template(record);
        }

        return result;
    }
}

ColumnStore.registerColumnType(ResourceInfoColumn);
