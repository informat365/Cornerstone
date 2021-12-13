<style scoped>
.workflow-form-box{
    padding-left:5px;
    margin-top:10px;
    margin-bottom:10px;
}
.workflow-form-value-box{
    display: flex;
    align-items: flex-start;
    margin-top:7px;
}
.form-name{
    width:100px;
    font-weight: normal;
}
.form-value{
    flex:1;
    font-weight: normal;
}

</style>
  
 <template>
 <div class="workflow-form-box">
    <div v-for="field in fieldList" :key="field.id" class="workflow-form-value-box">
        <div class="form-name text-no-wrap">{{field.name}}</div>
        <div class="form-value">
            <WorkflowDataLabel :formData="parsedFormData" :field="field"></WorkflowDataLabel>
        </div>
    </div>
 </div>
</template>
<script>
export default {
    props:['formData','formFieldList'],
    data() {
        return {
            parsedFormData:{},
            fieldList:[],
        };
    },
    mounted(){
        this.setupValue();
    },
    methods: {
        setupValue(){
            var form=JSON.parse(this.formData);
            this.fieldList=[];
            this.formFieldList.forEach(f=>{
                if(form[f.id]){
                    this.fieldList.push(f);
                }
            })
            this.parsedFormData=form;
        }
    }
};
</script>