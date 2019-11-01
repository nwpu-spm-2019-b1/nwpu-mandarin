<template>
    <div class="book-editor">
        <!--<h2 class="module-title">{{page_title}}</h2>-->
        <nav aria-label="breadcrumb">
            <ol class="breadcrumb">
                <li class="breadcrumb-item">
                    <router-link to="/">Librarian</router-link>
                </li>
                <li class="breadcrumb-item">
                    <router-link to="/books">Book management</router-link>
                </li>
                <li class="breadcrumb-item active" aria-current="page">
                    Book editor
                </li>
            </ol>
        </nav>
        <form @submit="submit" class="book-editor-form">
            <div class="form-group">
                <label for="isbn-input">ISBN</label>
                <input class="form-control" id="isbn-input" name="isbn" v-model="book.isbn"/>
                <a href="javascript:void(0);" @click="loadExternal" class="mt-3">Fetch data from Google Books</a>
                <br>
                <a href="javascript:void(0);" @click="onNoISBN" class="mt-3">This book does not have an ISBN</a>
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
                <div class="categories-container">
                    <div class="category-item" v-for="category in book.categories" v-bind:data-name="category">
                        {{category}}
                        <a href="javascript:void(0);" @click="removeCategory" v-html="'&times;'"></a>
                    </div>
                    <div v-if="book.categories.length===0">
                        No categories.
                    </div>
                </div>
                <datalist id="existing-categories">
                    <option v-for="category in all_categories" v-bind:value="category.name"></option>
                </datalist>
                <input class="form-control" id="new-category-input" type="text" list="existing-categories">
                <button type="button" @click="addCategory" class="btn btn-primary">Add</button>
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
            <button class="btn btn-secondary" @click="(e)=>{e.preventDefault();$router.back();}" type="button">
                Go back
            </button>
            <button class="btn btn-warning" @click="(e)=>{e.preventDefault();clear();}">Clear</button>
        </form>
        <div class="add-book-success" v-if="add_book.success">
            <h3>{{add_book.books.length}} books successfully added:</h3>
            <canvas v-for="book in add_book.books" v-bind:id="'barcode-'+book.id" width="8vw"></canvas>
        </div>
    </div>
</template>
<script>
    import {pickProperties} from '../js/common.js';
    import {urlWithParams} from "../js/common";

    const defaultBookData = {
        isbn: '',
        title: '',
        author: '',
        description: '',
        location: '',
        price: '',
        categories: [],
        count: 1
    };

    function padNumber(num, length) {
        let s = num.toString();
        while (length > s.length) {
            s = "0" + s;
        }
        return s;
    }

    export default {
        data: function () {
            return {
                book_id: this.$route.name !== "add-book" ? (this.$route.params.id) : null,
                book: {
                    ...defaultBookData
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

                } else {
                    this.page_title = "Add books";
                    this.submit_text = "OK";
                }
                await this.loadAllCategories();
            })();
        },
        methods: {
            addCategory(e) {
                let category_name = $("#new-category-input").val();
                if (!this.book.categories.includes(category_name)) {
                    this.book.categories.push(category_name);
                }
            },
            removeCategory(e) {
                this.book.categories.splice(this.book.categories.indexOf(e.target.dataset.name), 1);
            },
            onNoISBN: async function (e) {
                let resp = await fetch("/api/librarian/generate?type=synth_isbn", {
                    credentials: "same-origin"
                });
                let body = await resp.json();
                if (!resp.ok) {
                    throw new Error(body.message);
                }
                this.book.isbn = body.data;
            },
            loadExternal: async function () {
                let resp = await fetch(urlWithParams("http://106.13.1.40:5000/googleApi", {isbn: this.book.isbn}));
                let body = await resp.json();
                if (!resp.ok) {
                    throw new Error(body.message);
                }
                console.log(JSON.stringify(body));
                if (body.items.length === 0) {
                    return;
                }
                let info = body.items[0].volumeInfo;
                let book = {};
                book.title = info.title;
                book.author = info.authors.join(', ');
                book.description = info.description;
                Object.assign(this.book, book);

            },
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
                this.book.categories = this.book.categories.map(item => item.name);
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
            clear() {
                this.book = {...defaultBookData};
            },
            submit: async function (event) {
                event.preventDefault();
                let data = pickProperties(this.book, ["isbn", "title", "author", "description", "categories", "location", "price", "count"]);
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
                    this.error = null;
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
                                let text = padNumber(book.id, 10);
                                let canvas = document.createElement("canvas");
                                // let canvas = $(`#barcode-${book.id}`);
                                JsBarcode(canvas, text);
                                let out = $(`#barcode-${book.id}`)[0];
                                out.height = canvas.height + 30;
                                out.width = canvas.width;
                                let context = out.getContext("2d");
                                context.drawImage(canvas, 0, 0);
                                context.font = '12pt monospace';
                                context.textAlign = 'center';
                                context.fillStyle = 'black';
                                context.fillText("" + book.location, out.width / 2, out.height - 10);
                            });
                            $(".add-book-success")[0].scrollIntoView(true);
                        });
                    }
                } catch (err) {
                    this.error = err.message;
                }
            }
        }
    }
</script>
<style>
</style>