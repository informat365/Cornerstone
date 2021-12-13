<style scoped>
.workflow-form{
    padding:10px;
}
.form-title{
    font-size:18px;
    font-weight: bold;
    color:#333;
    margin-top:10px;
    text-align: center;
}
.form-subtitle{
    font-size:13px;
    color:#666;
    margin-top:10px;
    text-align: center;
}
.form-item-box{
    padding:10px;
    display: flex;
    flex-wrap: wrap;
}

.form-item-holder{
    display: inline-block;
    margin-bottom:10px;
    width:100%;
}
.form-item-title{
    font-size:13px;
    font-weight: normal;
    color:#666;
}
.form-item-value{
    margin:10px 0;
    font-size:13px;
    font-weight: bold;
}
.form-item-desc{
    font-size:12px;
    font-weight: normal;
    color:#999;
    margin-left:5px;
    display: inline-block;
}
.form-item-holder-static-groupitem{
    font-weight: bold;
    color:#333;
    font-size:16px;
    margin: 15px 0;
    padding-left:7px;
    border-bottom:1px solid #eee;
    position: relative;
    padding-bottom:5px;
}
.form-item-holder-static-groupitem::before{
    height:15px;
    width:4px;
    display: block;
    background:  rgba(35,145,255,1);
    content: '';
    position: absolute;
    top:4px;
    left:0;
}
.form-item-holder-static-labelitem{
    font-size:13px;
    color:#333;
}
.required{
    color:#C7000B;
    margin:0 3px;
}
.form-item-holder-static-alert-success{
    border: 1px solid #8ce6b0;
    background-color: #edfff3;
    font-size:13px;
    padding:10px;
}
.form-item-holder-static-alert-warning{
    border: 1px solid #ffd77a;
    background-color: #fff9e6;
    font-size:13px;
    padding:10px;
}
.form-item-holder-static-alert-error{
    border: 1px solid #ffb08f;
    background-color: #ffefe6;
    font-size:13px;
    padding:10px;
}
.form-item-holder-static-alert-info{
    border: 1px solid #abdcff;
    background-color: #f0faff;
    font-size:13px;
    padding:10px;
}
.form-form{
    margin-top:7px;
}
</style>
  
 <template>
    <div class="workflow-form">
        <div class="form-title">{{form.title}}</div>
        <div v-if="form.subTitle" class="form-subtitle">{{form.subTitle}}</div>
        
        <div class="form-form" ref="form" :model="value" :rules="formRule">
            <div class="form-item-box">
                <template v-for="item in fieldList">
                    <div class="form-item-holder" :key="item.id">
                        <div v-if="item.type.indexOf('static')==-1" :prop="item.id">
                            <div class="form-item-title">{{item.name}} 
                                <span class="form-item-desc">{{item.remark}}</span>
                            </div>
                            <div class="form-item-value">
                                <WorkflowDataLabel :field="item" :formData="value"></WorkflowDataLabel>
                            </div>
                        </div>

                        <template v-if="item.type=='static-group'">
                            <div class="form-item-holder-static-groupitem">{{item.name}}</div>
                        </template>
                        <template v-if="item.type=='static-label'">
                            <div class="form-item-holder-static-labelitem">
                                <RichtextLabel :value="item.content"></RichtextLabel>
                            </div>
                        </template>
                         <template v-if="item.type=='static-alert'">
                            <div :class="'form-item-holder-static-alert-'+item.alertType">{{item.remark}}</div>
                        </template>

                    </div>
                 </template>
            </div>
        </div>
    </div>
</template>


<script>
export default {
    props:['form','value','formFieldList'],
    data() {
        return {
            fieldList:[],
            formRule:{}
        };
    },
    mounted(){
        this.setupValue();
    },
    methods: {
        setupValue(){
            var fList=JSON.parse(this.form.fieldList);
            var allGraphFields={};
            this.formFieldList.forEach(item=>{
                allGraphFields[item.id]=item;
            })
            var filteredList=[];
            fList.forEach(item=>{
                var gItem=allGraphFields[item.id];
                if(gItem){
                    if(gItem.visible){
                        item.editable=gItem.editable;
                        filteredList.push(item);   
                    }
                }
            })
            this.fieldList=filteredList
        }
    }
};
</script>