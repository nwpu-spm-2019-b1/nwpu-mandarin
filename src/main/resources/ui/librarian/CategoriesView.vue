<template>
    <div>
        <h1 class="module-title">Manage categories</h1>
        <div class="alert alert-danger" v-if="error!==null">{{error}}</div>
        <form action="#" method="post" @submit="addCategory" class="form-inline mb-2">
            <div class="form-group mr-3">
                <label class="sr-only" for="new-category-name-input">Name of new category</label>
                <input id="new-category-name-input" type="text" v-model="new_category_name" class="form-control">
            </div>
            <button class="btn btn-success">Add</button>
        </form>
        <span class="mb-2">There are {{categories.length}} categories.</span>
        <ul class="list-group">
            <li v-for="category in categories" v-bind:data-id="category.id"
                class="list-group-item d-flex flex-row"
                style="width: 40%; justify-content: space-between; align-items: center;">
                <div>{{category.name}}</div>
                <a href="javascript:void(0);" @click="deleteCategory">Delete</a>
            </li>
        </ul>
    </div>
</template>
<script>
    export default {
        data: function () {
            return {
                categories: [],
                error: null,
                new_category_name: ''
            }
        },
        mounted() {
            this.loadCategories();
        },
        methods: {
            loadCategories: async function () {
                let resp = await fetch("/api/librarian/categories", {
                    credentials: "same-origin"
                });
                let body = await resp.json();
                if (!resp.ok) {
                    this.error = body.message;
                    return;
                }
                this.categories = body.data;
            },
            addCategory: async function (event) {
                event.preventDefault();
                let data = new FormData();
                data.append("name", this.new_category_name);
                let resp = await fetch("/api/librarian/categories", {
                    method: "POST",
                    credentials: "same-origin",
                    body: data
                });
                let body = await resp.json();
                if (!resp.ok) {
                    this.error = body.message;
                    return;
                }
                this.new_category_name = "";
                this.error = null;
                this.loadCategories();
            },
            deleteCategory: async function (event) {
                let resp = await fetch("/api/librarian/categories/" + event.target.parentNode.dataset.id, {
                    method: "DELETE",
                    credentials: "same-origin"
                });
                let body = await resp.json();
                if (!resp.ok) {
                    this.error = body.message;
                    return;
                }
                this.error = null;
                this.loadCategories();
            }
        }
    }
</script>
<style>
</style>