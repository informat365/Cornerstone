<style scoped>
.sparkline {
  stroke: red;
  fill: none;
}

/* line with highlight area */
.sparkline {
  stroke: #009CF1;
  fill: rgba(0, 156,241, .3);
}

/* change the spot color */
.sparkline--spot {
  stroke: blue;
  fill: blue;
}

/* change the cursor color */
.sparkline--cursor {
  stroke: orange;
}

/* style fill area and line colors using specific class name */
.sparkline--fill {
  fill: rgba(255, 0, 0, .3);
}

.sparkline--line {
  stroke: red;
}
</style>
<template>
   <svg ref="sparkline" class="sparkline" :width="svgWidth" :height="svgHeight" stroke-width="2"></svg>
</template>
<script>
import sparkline from "@fnando/sparkline"
export default {
    name:"Sparkline",
    props: ['value','width','height'],
    data (){
        return{
            svgWidth:100,
            svgHeight:30
        }
    },
    mounted(){
        if(this.width){
            this.svgWidth=this.width;
        }
        if(this.height){
            this.svgHeight=this.height;
        }
        this.$nextTick(()=>{
            this.setupValue();
        })
    },
    watch:{
        value(val){
            this.setupValue();
        }
    },
    methods:{
        setupValue(){
            if(this.value){
                var t=0;
                for(var i=0;i<this.value.length;i++){
                    t+=this.value[i];
                }
                if(t>0){
                    sparkline(this.$refs.sparkline, this.value,{
                        interactive:false
                    });
                }
            }
        }
    }
}
</script>