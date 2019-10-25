<template>
    <div class="news-management">
        <h1 class="module-title">Manage news</h1>
        <div class="post-news-form-container">
            <h2>Post news</h2>
            <div class="alert" v-if="post_news.message!==null"
                 v-bind:class="post_news.message.ok ? 'alert-success' : 'alert-danger'">
                {{post_news.message.content}}
            </div>
            <form action="#" method="post" @submit.prevent="postNews">
                <div class="form-group">
                    <label for="title-input">Title</label>
                    <input id="title-input" type="text" class="form-control" v-model="post_news.title">
                </div>
                <div class="form-group">
                    <label for="content-input">Content</label>
                    <textarea id="content-input" class="form-control" v-model="post_news.content">
                    </textarea>
                </div>
                <button type="submit" class="btn btn-primary">OK</button>
            </form>
        </div>
        <div class="news-list">
            <h3>News ({{list.length}})</h3>
            <div class="card" v-for="item in list">
                <div class="card-header">{{item.title}}</div>
                <div class="card-body">
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
                list: [],
                post_news: {
                    title: '',
                    content: '',
                    message: null
                }
            }
        },
        mounted() {
            this.loadNews();
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
            },
            postNews: async function () {
                let resp = await fetch("/api/librarian/news", {
                    method: "POST",
                    credentials: "same-origin",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({
                        title: this.post_news.title,
                        content: this.post_news.content
                    })
                });
                let body = await resp.json();
                this.post_news.message = {ok: resp.ok, content: body.message};
                this.loadNews();
            }
        }
    }
</script>

<style>

</style>