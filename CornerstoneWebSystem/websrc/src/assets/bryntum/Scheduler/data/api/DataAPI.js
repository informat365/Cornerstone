import Base from '../../../Core/Base.js';
import { base } from '../../../Core/helper/MixinHelper.js';
// APIs
import EventAPI from './EventAPI.js';
import AssignmentAPI from './AssignmentAPI.js';
import DependencyAPI from './DependencyAPI.js';
import ResourceAPI from './ResourceAPI.js';
// TODO: remove this
//import BatchAPI from './BatchAPI.js';

/**
 * @module Scheduler/data/api/DataAPI
 */

/**
 * This mixin combines all data layer APIs and provides a way to call API method regardless
 * if it conflicts with host class method.
 *
 * @mixin
 */
export default Target => {

    // Add new APIs here
    const APIs = [
        AssignmentAPI,
        DependencyAPI,
        EventAPI,
        ResourceAPI
        //TODO: remove this
        //BatchAPI
    ];

    const RAW_API = base(Base).mixes(...APIs);

    return class DataAPI extends (Target || Base) {
        /**
         * Data layer API gateway
         *
         * @property {Object}
         */
        get dataApi() {
            if (!this._dataApi) {
                this._dataApi = new RAW_API({ host : this.dataApiHost || this });
            }
            return this._dataApi;
        }
    };
};
