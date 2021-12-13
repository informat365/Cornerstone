
import Vue from 'vue'
const requireComponent = require.context(
    './',
    true
)
requireComponent.keys().forEach(fileName => {
    const componentConfig = requireComponent(fileName)
    var componentName = fileName.replace(/^\.\/(.*)\.\w+$/, '$1')
    var idx=componentName.lastIndexOf('/');
    if(idx!=-1){
      componentName=componentName.substring(idx+1);
    }
    console.log("register component:",componentName);
    window[componentName]=Vue.component(
      componentName,
      componentConfig.default || componentConfig
    )
})