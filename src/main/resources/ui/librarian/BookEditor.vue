<template>
    <div>
        <h2 class="module-title">{{page_title}}</h2>
        <form @submit="submit" class="book-editor-form">
            <div class="form-group">
                <label for="isbn-input">ISBN</label>
                <input class="form-control" id="isbn-input" name="isbn" v-model="book.isbn"/>
            </div>
            <div class="form-group">
                <label for="title-input">Title</label>
                <input class="form-control" id="title-input" name="title" v-model="book.title"/>
            </div>
            <div class="form-group">
                <label for="author-input">Author(s)</label>
                <input class="form-control" id="author-input" name="author" v-model="book.author"/>
            </div>
            <div class="form-group">
                <label for="description-input">Description</label>
                <textarea class="form-control" id="description-input" name="description" v-model="book.description">
                </textarea>
            </div>
            <div class="form-group">
                <label for="location-input">Location</label>
                <input class="form-control" id="location-input" name="location" v-model="book.location"/>
            </div>
            <div class="form-group">
                <label>Categories</label>
                <div class="category-item" v-for="category in book.categories">
                    {{category.name}}
                </div>
                <datalist id="existing-categories">
                    <option v-for="category in all_categories" v-bind:value="category.name"></option>
                </datalist>
                <input class="form-control" id="new-category-input" type="text" list="existing-categories">
            </div>
            <div class="form-group">
                <label for="price-input">Price</label>
                <input class="form-control" id="price-input" name="price" v-model="book.price"/>
            </div>
            <div class="form-group" v-if="book_id===null">
                <label for="count-input">Number of books</label>
                <input type="number" min="0" class="form-control" id="count-input" name="count" v-model="book.count"/>
            </div>
            <div id="error" class="alert alert-danger" v-if="error!==null">{{error}}</div>
            <button type="submit" class="btn btn-success btn">{{submit_text}}</button>
            <button class="btn btn-secondary" @click="$router.back()">Go back</button>
        </form>
        <div class="add-book-success" v-if="add_book.success">
            <h3>{{add_book.count}} books successfully added:</h3>
            <svg v-for="book in add_book.books" v-bind:id="'barcode-'+book.id"></svg>
        </div>
    </div>
</template>
<script>
    import {pickProperties} from '../js/common.js'

    export default {
        data: function () {
            return {
                book_id: this.$route.name !== "add-book" ? (this.$route.params.id) : null,
                book: {
                    isbn: '',
                    title: '',
                    author: '',
                    description: '',
                    location: '',
                    price: '',
                    categories: [],
                    count: 1
                },
                all_categories: [],
                error: null,
                page_title: 'Editor',
                submit_text: 'Submit',
                add_book: {
                    success: false,
                    books: []
                }
            };
        },
        mounted() {
            (async () => {
                if (this.book_id !== null) {
                    this.page_title = "Edit book";
                    this.submit_text = "Save changes";
                    await this.loadBookDetails();
                    await this.loadAllCategories();
                } else {
                    this.page_title = "Add books";
                    this.submit_text = "OK";
                }
            })();
        },
        methods: {
            loadBookDetails: async function () {
                let resp = await fetch("/api/book/" + this.book_id, {
                    method: "GET",
                    credentials: "same-origin"
                });
                let body = await resp.json();
                if (!resp.ok) {
                    throw new Error(body.message);
                }
                console.log(JSON.stringify(body.data));
                Object.assign(this.book, body.data);
                console.log(this.book);
                return body;
            },
            loadAllCategories: async function () {
                try {
                    let resp = await fetch("/api/librarian/categories", {
                        method: "GET",
                        credentials: "same-origin"
                    });
                    let body = await resp.json();
                    if (!resp.ok) {
                        throw new Error(body.message);
                    }
                    this.all_categories = body.data;
                } catch (err) {
                    window.showErrorToast(err.message);
                }
            },
            submit: async function (event) {
                event.preventDefault();
                let data = pickProperties(this.book, ["isbn", "title", "author", "description", "location", "price", "count"]);
                Object.assign(data, {
                    category_ids: []
                });
                console.log(JSON.stringify(data));
                let url, method;
                if (this.book_id !== null) {
                    url = "/api/librarian/book/" + this.book_id;
                    method = "PUT";
                } else {
                    url = "/api/librarian/book";
                    method = "POST";
                }
                try {
                    let resp = await fetch(url, {
                        method: method,
                        headers: {
                            "Content-Type": "application/json"
                        },
                        body: JSON.stringify(data),
                        credentials: "same-origin"
                    });
                    let body = await resp.json();
                    if (!resp.ok) {
                        throw new Error(body.message);
                    }
                    if (this.book_id !== null) {
                        if (this.$router.history.length >= 2) {
                            this.$router.history.go(-1);
                        } else {
                            this.$router.push({path: "/books"});
                        }
                    } else {
                        this.add_book.success = true;
                        this.add_book.books = body.data;
                        this.$nextTick(function () {
                            this.add_book.books.map((book) => {
                                $(`#barcode-${}`)
                            });
                        });
                    }
                } catch (err) {
                    this.error = err.message;
                }
            }
        }
    }
</script>
<style scoped>
</style>