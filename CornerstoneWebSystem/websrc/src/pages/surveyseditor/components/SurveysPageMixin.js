export default{
	data(){
		return {
            isSaved:true
		}
	},
    watch:{
        isSaved(val){
            app.postMessage('surveys.isSaved',val);
        }
    },
	methods:{
        isPageSaved(){
            return this.isSaved;
        },
        save(callback){
            this.confirm(callback);
        },
        watchSaveProp(prop){
            this.$watch(prop, ()=>{
                this.isSaved=false;
            }, {deep: true})
        }
	}
}