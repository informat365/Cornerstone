<style scoped>
</style>

<i18n>
    {
    "en": {
    "更新License":"Update License",
    "License内容":"License Content"
    },
    "zh_CN": {
    "更新License":"更新License",
    "License内容":"License内容"
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
        :title="$t('更新License')"
        width="600"
        @on-ok="confirm"
    >
        <Form
            class="left"
            @submit.native.prevent
            ref="form"
            :rules="formRule"
            :model="formItem"
            label-position="top"
        >
            <FormItem label="License" prop="content">
                <Input type="textarea"  :rows="7" v-model.trim="formItem.content" :placeholder="$t('License内容')"></Input>
            </FormItem>
        </Form>
        <div slot="footer">
            <Row>
                <Col span="24" style="text-align:right">
                    <Button @click="confirm" type="default" size="large">{{$t('保存')}}</Button>
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
                content:null,
            },
            formRule: {
                content: [vd.req]
            }
        };
    },
    methods: {
        pageLoad() {

        },
        confirm() {
            this.$refs.form.validate(r => {
                if (r) this.confirmForm();
            });
        },
        confirmForm() {
            app.invoke("BizAction.updateCompanyLicense", [app.token,
                this.args.id,this.formItem.content], info => {
                app.postMessage("company.update");
                this.showDialog = false;
            });
        }
    }
};
</script>
