/**
 * @module Core/data/stm/Props.js
 * @internal
 */

export const STATE_PROP             = Symbol('STATE_PROP');
export const STORES_PROP            = Symbol('STORES_PROP');
export const QUEUE_PROP             = Symbol('QUEUE_PROP');
export const POS_PROP               = Symbol('POS_PROP');
export const TRANSACTION_PROP       = Symbol('TRANSACTION_PROP');
export const TRANSACTION_TIMER_PROP = Symbol('TRANSACTION_TIMER_PROP');
export const AUTO_RECORD_PROP       = Symbol('AUTO_RECORD_PROP');
export const PROPS                  = Object.freeze([STATE_PROP, STORES_PROP, QUEUE_PROP, POS_PROP, TRANSACTION_PROP, TRANSACTION_TIMER_PROP, AUTO_RECORD_PROP]);
