import GridHeader from '../../Grid/view/Header.js';

/**
 * @module Scheduler/view/Header
 */

/**
 * Custom header subclass which handles the existence of the special TimeAxisColumn
 *
 * @extends Grid/view/Header
 * @private
 */
export default class Header extends GridHeader {
    refreshContent() {
        // Only render contents into the header once as it contains the special rendering of the TimeAxisColumn
        if (this.headersElement && this.headersElement.childNodes.length === 0) {
            super.refreshContent();
        }
    }
}
