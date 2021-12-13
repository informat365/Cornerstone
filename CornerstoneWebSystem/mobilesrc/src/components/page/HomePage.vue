<template>
  <div class="page">  
      <group >
            <flexbox style="padding:20px">
            <flexbox-item :span="2">
                 <AvatarImage  size="large" 
                :name="account.name" 
                :value="account.imageId" type="tips">
                </AvatarImage>
            </flexbox-item>
             <flexbox-item :span="10">
                 <div>{{account.name}}</div>
                 <div class="company-name">{{company.name}}</div>
            </flexbox-item>
        </flexbox>
    </group>

     <group>
        <cell title="CORNERSTONE" :value="version"></cell>
    </group>
       
  </div>
</template>

<script>
import { Flexbox, FlexboxItem,Group,GroupTitle,Cell} from 'vux'
export default {
    components: {Flexbox,FlexboxItem,Group,GroupTitle,Cell},
    mixins:[componentMixin],
    data () {
        return {
            account:{},
            company:{},
            version:null
        }
    },
    methods:{
        pageLoad(){
            this.account=app.account;
            this.company=app.company;
            this.version=app.version;
        },
        loadData(){
            
        }
    }
}
</script>

<style scoped>
    .page{
       padding:0;
    }
    .company-name{
        color:#999;
    }
</style>