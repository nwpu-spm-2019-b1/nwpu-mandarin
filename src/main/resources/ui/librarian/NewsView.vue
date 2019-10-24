<template>
    <div class="news-management">
        <h1 class="module-title">Manage news</h1>
        <div>
            <div class="card" v-for="item in list">
                <div class="card-header">{{item.title}}</div>
                <div class="card-text">
                    {{item.content}}
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    export default {
        data: function () {
            return {
                list: []
            }
        },
        methods: {
            loadNews: async function () {
                let resp = await fetch("/api/librarian/news", {
                    credentials: "same-origin"
                });
                let body = await resp.json();
                if (!resp.ok) {
                    alert(body.message);
                    return;
                }
                this.list = body.data;
            }
        }
    }
</script>

<style>

</style>