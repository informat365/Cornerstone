<template>
	<div class="cv-header">
		<div class="cv-header-nav">	
			<IconButton :disabled="!headerProps.previousPeriod" icon="md-arrow-back" @click="onInput(headerProps.previousPeriod)"></IconButton>
          	<div class="periodLabel">
				<slot name="label"/>
			</div>
			<IconButton :disabled="!headerProps.nextPeriod" icon="md-arrow-forward" @click="onInput(headerProps.nextPeriod)"></IconButton>
			<IconButton  @click="onInput(headerProps.currentPeriod)"  title="今天"></IconButton>	


			<IconButton  @click="onViewChange('month')"  title="月视图"></IconButton>	
			<IconButton  @click="onViewChange('week')"  title="周视图"></IconButton>	

		</div>
		
	</div>
</template>
<script>
export default {
	name: "CalendarViewHeader",
	props: {
		headerProps: {
			type: Object,
			required: true,
		},
	},
	methods: {
		onInput(d) {
			this.$emit("input", d)
		},
		onViewChange(d){
			this.$emit("view-changed", d)
			
		}
	},
}
</script>
<style>
.cv-header {
	display: flex;
	flex: 0 1 auto;
	flex-flow: row nowrap;
	align-items: center;
	min-height: 2.5em;
	border-width: 1px 1px 0 1px;
}
.cv-header .periodLabel {
	display: inline-flex;
	flex: 1 1 auto;
	flex-flow: row nowrap;
	min-height: 1.5em;
	line-height: 1;
	font-size: 1.5em;
}
.cv-header,
.cv-header button {
	border-style: solid;
	border-color: #ddd;
}
.cv-header-nav,
.cv-header .periodLabel {
	margin: 0.1em 0.6em;
}
.cv-header-nav button,
.cv-header .periodLabel {
	padding: 0.4em 0.6em;
}
.cv-header button {
	box-sizing: border-box;
	line-height: 1em;
	font-size: 1em;
	border-width: 1px;
}
</style>