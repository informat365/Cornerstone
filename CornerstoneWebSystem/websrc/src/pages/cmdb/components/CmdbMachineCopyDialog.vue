<style scoped>
</style>
<i18n>
{
	"en": {
        "复制到项目": "复制到项目",
		"主机修改后，复制的主机也会同步修改": "主机修改后，复制的主机也会同步修改",
		"将主机复制到以下项目": "将主机复制到以下项目",
		"选择项目": "选择项目",
		"复制": "复制",
		"请选择要复制到的项目": "请选择要复制到的项目",
		"复制成功": "复制成功"
    },
	"zh_CN": {
		"复制到项目": "复制到项目",
		"主机修改后，复制的主机也会同步修改": "主机修改后，复制的主机也会同步修改",
		"将主机复制到以下项目": "将主机复制到以下项目",
		"选择项目": "选择项目",
		"复制": "复制",
		"请选择要复制到的项目": "请选择要复制到的项目",
		"复制成功": "复制成功"
	}
}
</i18n>
 <template>
    <Modal
        ref="dialog"
        v-model="showDialog"
        :closable="true"
        :mask-closable="false"
        :loading="false"
        :title="$t('复制到项目')"
        width="700"
    >
        <Form label-position="top" style="height:500px;padding:15px">
            <Alert>{{$t('主机修改后，复制的主机也会同步修改')}}</Alert>

            <FormItem :label="$t('将主机复制到以下项目')">
                <Select
                    filterable
                    transfer
                    multiple
                    style="width:100%"
                    :placeholder="$t('选择项目')"
                    v-model="formItem.projectList"
                >
                    <Option
                        v-for="item in projectList"
                        :key="item.id"
                        :value="item.id"
                    >{{item.name}}</Option>
                </Select>
            </FormItem>
        </Form>
        <div slot="footer">
            <Row>
                <Col span="24" style="text-align:right">
                    <Button @click="confirmForm" type="default" size="large">{{$t('复制')}}</Button>
                </Col>
            </Row>
        </div>
    </Modal>
</template>


<script>
export default {
    mixins: [componentMixin],
    data() {
        return {
            formItem: {
                projectList: []
            },
            projectList: []
        };
    },
    methods: {
        pageLoad() {
            this.loadProjectList();
        },
        loadProjectList() {
            var query = {
                pageIndex: 1,
                pageSize: 1000
            };
            app.invoke(
                "BizAction.getAllRunningProjectList",
                [app.token, query],
                info => {
                    this.projectList = info.list;
                }
            );
        },
        confirmForm() {
            if (this.formItem.projectList.length == 0) {
                app.toast(this.$t("请选择要复制到的项目"));
                return;
            }
            app.invoke(
                "BizAction.createMachineListFromCmdbMachine",
                [app.token, this.args.machineId, this.formItem.projectList],
                info => {
                    app.toast(this.$t("复制成功"));
                    this.showDialog = false;
                }
            );
        }
    }
};
</script>