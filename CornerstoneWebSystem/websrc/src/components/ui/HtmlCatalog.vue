<template>
  <transition name="fade">
    <div
      ref="htmlCatalog" class="html-catalog scrollbox" :style="catalogStyle" v-show="catalogShow">
      <div
        class="menu">
        <ul class="menu-1">
          <template v-for="item in catalog">
            <li
              :key="item.id"
              :ref="'catalog-li-'+item.id"
              :class="{'active': activeCatalogId === item.id}"
              @click.stop.prevent="onClickCatalogItem(item)">
              <div
                :title="item.name" class="text-ellipsis">
                {{item.name}}
              </div>
              <template v-if="!isEmptyArray(item.children)">
                <ul
                  class="menu-2" @click.stop.prevent="onClickCatalogItem(item)">
                  <template v-for="child in item.children">
                    <li
                      :key="child.id"
                      :ref="'catalog-li-'+child.id"
                      :class="{'active': activeCatalogId === child.id}"
                      @click.stop.prevent="onClickCatalogItem(child)">
                      <div
                        :title="child.name" class="text-ellipsis">
                        {{child.name}}
                      </div>
                    </li>
                  </template>
                </ul>
              </template>
            </li>
          </template>
        </ul>
      </div>
    </div>
  </transition>
</template>
<i18n>
  {
  "en": {
  "目录":"Catalogue"
  },
  "zh_CN": {
  "目录":"目录"
  }
  }
</i18n>
<script>

  import VueScrollTo from 'vue-scrollto';
  import { debounce, off, on } from './HtmlCatalogService';

  export default {
    name: 'HtmlCatalog',
    props: {
      scrollBox: {
        type: Element,
      },
      scrollTop: {
        type: Number,
        default: 0,
      },
      offsetTop: {
        type: Number,
        default: 100,
      },
      offsetLeft: {
        type: Number,
        default: 0,
      },
    },
    data() {
      return {
        catalog: [],
        catalogEls: [],
        activeCatalogId: 0,
        menuMarginTop: 0,
        htmlBoxWidth: 0,
        bodyWidth: 0,
      };
    },
    computed: {
      catalogFixRight() {
        return (this.bodyWidth - this.offsetLeft - this.htmlBoxWidth) / 2 - 270;
      },
      catalogShow() {
        return !this.isEmptyArray(this.catalog) && this.catalogFixRight > 10;
      },
      catalogStyle() {
        if (this.catalogFixRight < 10) {
          return {
            visibility: 'hidden',
            right: '0px',
          };
        }
        return {
          visibility: 'visible',
          top: `${ this.offsetTop }px`,
          height: `calc(100vh - ${ this.offsetTop + 100 }px)`,
          right: `${ this.catalogFixRight }px`,
        };
      },
    },
    watch: {
      scrollBox: {
        immediate: true,
        handler(el, oldEl) {
          if (oldEl) {
            off(oldEl, 'scroll', this.debounceHandleScroll);
          }
          if (el) {
            on(el, 'scroll', this.debounceHandleScroll);
          }
        },
      },
    },
    created() {
      this.catalog = [];
      this.catalogEls = [];
      this.activeCatalogId = 0;
      this.debounceAnalyzeCatalog = debounce(this.analyzeCatalog, 500);
      this.debounceHandleScroll = debounce(this.onScroll, 250);
      this.debounceHandleResize = () => {
        this.bodyWidth = document.body.getBoundingClientRect().width;
      };
      on(window, 'resize', this.debounceHandleResize);
      this.debounceHandleResize();
    },
    destroyed() {
      this.catalog = [];
      this.catalogEls = [];
      this.activeCatalogId = 0;
      this.bodyWidth = 0;
      if (this.scrollBox) {
        off(this.scrollBox, 'scroll', this.debounceHandleScroll);
      }
      off(window, 'resize', this.debounceHandleResize);
      this.debounceAnalyzeCatalog = null;
      this.debounceHandleScroll = null;
      this.debounceHandleResize = null;
    },
    methods: {
      isEmptyArray(array) {
        return !array || !Array.isArray(array) || array.length === 0;
      },
      isEmptyString(value) {
        if (value === 0) {
          return false;
        }
        return !value || value.toString().trim().length === 0;
      },
      analyzeElHtml(el) {
        this.catalog = [];
        if (!el) {
          return;
        }
        this.debounceAnalyzeCatalog(el);
        this.htmlBoxWidth = el.getBoundingClientRect().width;
      },
      analyzeHTag(cel, tagName) {
        if (!cel) {
          return;
        }
        const els = cel.getElementsByTagName(tagName);
        if (els.length === 0) {
          return;
        }
        for (let i = 0; i < els.length; i++) {
          const hel = els[i];
          if (this.isEmptyString(hel.className)) {
            hel.className = `h-tag-scroll`;
          } else {
            hel.className += ` h-tag-scroll`;
          }
        }
      },
      analyzeCatalog(cel) {
        if (!cel) {
          return;
        }
        this.analyzeHTag(cel, 'h1');
        this.analyzeHTag(cel, 'h2');
        this.analyzeHTag(cel, 'h3');
        this.analyzeHTag(cel, 'h4');
        this.analyzeHTag(cel, 'h5');
        this.analyzeHTag(cel, 'h6');
        const hels = cel.getElementsByClassName('h-tag-scroll');
        if (hels.length === 0) {
          return;
        }
        if (hels.length === 0) {
          return;
        }
        const hLevel = {
          H1: 1,
          H2: 2,
          H3: 3,
          H4: 4,
          H5: 5,
          H6: 6,
        };
        const catalog = [];
        const catalogEls = [];
        let lastHLevel = 7;
        let secondLevel = 7;
        let children = [];
        for (let i = 0; i < hels.length; i++) {
          const el = hels[i];
          if (this.isEmptyString(el.className)) {
            el.className = el.className.replace(' h-tag-scroll', '').replace('h-tag-scroll', '');
          }
          const nodeName = el.nodeName;
          if (!nodeName) {
            continue;
          }
          const innerText = el.innerText.trim();
          if (this.isEmptyString(innerText)) {
            continue;
          }
          const elLevel = hLevel[el.nodeName];
          if (elLevel <= lastHLevel) {
            const item = catalog[catalog.length - 1];
            if (item) {
              item.children = children;
            }
            children = [];
            lastHLevel = elLevel;
            secondLevel = 7;
            catalog.push({
              id: i + 1,
              name: innerText,
              children: [],
              el,
            });
          } else {
            if (elLevel > secondLevel) {
              continue;
            }
            secondLevel = elLevel;
            children.push({
              id: i + 1,
              name: innerText,
              el,
            });
          }
        }
        if (!this.isEmptyArray(children)) {
          const item = catalog[catalog.length - 1];
          if (item) {
            item.children = children;
          }
        }
        catalog.forEach(item => {
          catalogEls.push({
            id: item.id,
            offsetTop: item.el.offsetTop,
          });
          if (this.isEmptyArray(item.children)) {
            return;
          }
          item.children.forEach(child => {
            catalogEls.push({
              id: child.id,
              offsetTop: child.el.offsetTop,
            });
          });
        });
        if (!this.isEmptyArray(catalog)) {
          catalog.unshift({
            id: 0,
            name: this.$t('目录'),
            el: this.scrollBox,
            offsetTop: 0,
          });
        }
        this.catalog = catalog;
        this.catalogEls = catalogEls;
      },
      onClickCatalogItem(item) {
        if (!item.el) {
          return;
        }
        this.activeCatalogId = item.id;
        this.scrollTo(this.scrollBox, item.el);
      },
      scrollTo(container, el) {
        VueScrollTo.scrollTo(el, {
          container: container,
          duration: 500,
          easing: 'ease',
          offset: -50,
          force: true,
          cancelable: true,
          onStart: false,
          onDone: false,
          onCancel: false,
          x: false,
          y: true,
        });
      },
      onScroll() {
        const scrollTop = this.scrollBox.scrollTop;
        if (this.isEmptyArray(this.catalog)) {
          return;
        }
        if (scrollTop === 0) {
          this.activeCatalogId = 0;
          this.compMenuTop();
          return;
        }
        const matchEls = [];
        this.catalogEls.forEach(item => {
          if (item.id === 0) {
            return;
          }
          if (scrollTop > item.offsetTop) {
            matchEls.push({
              id: item.id,
              diff: scrollTop - item.offsetTop,
            });
          }
        });
        matchEls.sort((a, b) => {
          return Math.abs(a.diff) - Math.abs(b.diff);
        });
        const activeItem = matchEls[0];
        if (!activeItem) {
          return;
        }
        this.activeCatalogId = activeItem.id;
        this.compMenuTop();
      },
      compMenuTop() {
        const list = this.$refs[`catalog-li-${ this.activeCatalogId }`];
        if (this.isEmptyArray(list)) {
          return;
        }
        this.scrollTo(this.$refs.htmlCatalog, list[0]);
      },
    },
  };
</script>

<style lang="less" scoped>
  .text-ellipsis {
    text-overflow: ellipsis;
    -o-text-overflow: ellipsis;
    white-space: nowrap;
    overflow: hidden;
  }

  .html-catalog {
    position: fixed;
    top: 0;
    right: 0;
    width: 200px;
    overflow: auto;
    overflow-scrolling: touch;
    text-align: left;
    z-index: 11;
    visibility: hidden;

    &-vhtml {
      display: none;
    }

    .menu {
      position: relative;

      .menu-2 {
        margin-left: 20px;
        margin-top: 5px;
      }

      .menu-1 li,
      .menu-2 li {
        font-size: 13px;
        color: #666;
        cursor: pointer;
        margin-bottom: 7px;
        font-weight: normal;
      }

      .menu-1 li.active,
      .menu-2 li.active {
        font-weight: bold;
        color: #2391ff;
      }
    }
  }
</style>
