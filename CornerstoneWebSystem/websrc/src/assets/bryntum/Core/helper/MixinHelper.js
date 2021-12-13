import Base from '../Base.js';

//inspired by http://justinfagnani.com/2015/12/21/real-mixins-with-javascript-classes/

export const base = (baseClass) => (
    {
        mixes : (...mixins) => {
            return mixins.reduce((result, mixin) => mixin(result), baseClass || Base);
        }
    }
);
