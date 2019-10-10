<template>
    <div class="books-view">
        <h1 class="module-title">Book management</h1>
        <div class="action-buttons">
            <button class="btn btn-success" @click="startAddingBooks">Add</button>
            <button class="btn btn-primary" @click="$router.push({name:'lend-and-return'})">Lend / return book</button>
            <button class="btn btn-danger" @click="warnDeletingBooks">Delete all selected books</button>
        </div>
        <div class="modal fade" tabIndex="-1" role="dialog" id="delete-warning-modal">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Confirm your action</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true" v-html="'&times;'"></span>
                        </button>
                    </div>
                    <div class="modal-body">
                        <div>
                            You are deleting {{deletion.id_list.length}} book{{deletion.id_list.length > 1 ?'s':''}}.
                            Are you sure?
                        </div>
                        <div class="alert alert-danger" v-if="deletion.error!==null">{{deletion.error}}</div>
                    </div>
                    <div class="modal-footer">
                        <button class="btn btn-primary" @click="deleteBooks">Confirm</button>
                        <button class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                    </div>
                </div>
            </div>
        </div>
        <div class="search-box">
            <form action="#" @submit="(e)=>{e.preventDefault();this.loadBooks();}">
                <div style="display: flex; flex-direction: row;">
                    <div style="width: 10%;" class="input-container input-group">
                        <select name="type" class="form-control" v-model="search.type">
                            <option value="isbn">ISBN</option>
                            <option value="title" selected>Title</option>
                            <option value="author">Author</option>
                            <option value="description">Description</option>
                        </select>
                    </div>
                    <div style="width: 80%;" class="input-container input-group">
                        <input type="text" name="query" class="form-control" v-model="search.query"/>
                    </div>
                    <div style="width: 10%;" class="input-container input-group">
                        <button type="submit" class="btn btn-primary search-btn btn-block">
                            Search
                        </button>
                    </div>
                </div>
            </form>
        </div>
        <table class="table table-hover">
            <thead>
            <tr>
                <th></th>
                <th>#</th>
                <th>ISBN</th>
                <th>Title</th>
                <th>Author(s)</th>
                <th>Location</th>
                <th>Actions</th>
            </tr>
            </thead>
            <tbody>
            <template v-for="book in books">
                <tr v-bind:key="book.id" @click="showDescription" class="book-main-row">
                    <td>
                        <input type="checkbox" v-bind:data-id="book.id" class="row-checkbox"/>
                    </td>
                    <td>{{book.id}}</td>
                    <td>{{book.isbn}}</td>
                    <td>{{book.title}}</td>
                    <td>{{book.author}}</td>
                    <td>{{book.location}}</td>
                    <td>
                        <a href="javascript:void(0);" @click="editBook" v-bind:data-id="book.id">Edit</a>
                        <a href="javascript:void(0);" @click="warnDeletingBooks" v-bind:data-id="book.id">Delete</a>
                    </td>
                </tr>
                <tr v-bind:id="'info-'+book.id" v-bind:key="'info-'+book.id" style="display: none;"
                    class="book-info-row">
                    <td colspan="7">
                        <div class="info-item">
                            <label>Description:</label>
                            {{book.description}}
                        </div>
                        <div class="info-item">
                            <label>Price:</label>
                            {{book.price}}
                        </div>
                    </td>
                </tr>
            </template>
            </tbody>
        </table>
        <div class="pager-container" v-if="pager.total > 0">
            <nav aria-label="Page selector">
                <ul class="pagination justify-content-center">
                    <li class="page-item" v-bind:class="{'disabled':pager.current===1}">
                        <a class="page-link"
                           href="javascript:void(0);"
                           @click="changePage"
                           data-page="prev">
                            Previous
                        </a>
                    </li>
                    <li class="page-item" v-for="page in pager.total" v-bind:class="{'active':pager.current===page}">
                        <a class="page-link"
                           href="javascript:void(0);"
                           @click="changePage"
                           v-bind:data-page="page">
                            {{page}}
                        </a>
                    </li>
                    <li class="page-item" v-bind:class="{'disabled':pager.current===pager.total}">
                        <a class="page-link"
                           href="javascript:void(0);"
                           @click="changePage"
                           data-page="next">
                            Next
                        </a>
                    </li>
                </ul>
            </nav>
        </div>
    </div>
</template>
<script>
    import {urlWithParams} from "../js/common";

    require('bootstrap/js/src/modal.js');

    export default {
        methods: {
            loadBooks() {
                fetch(urlWithParams("/api/librarian/book/search", {
                    type: this.search.type,
                    query: this.search.query,
                    page: this.pager.current
                }), {
                    method: "GET",
                    credentials: "same-origin"
                }).then(async resp => {
                    let body = await resp.json();
                    if (!resp.ok) {
                        throw new Error(body.message);
                    }
                    this.books = body.data.books;
                    this.pager.total = body.data.total;
                    this.pager.count = body.data.count;
                }).catch(err => {
                    console.log(err.message);
                    this.onError({error: err.message});
                });
            },
            showDescription(event) {
                let element = $(event.target);
                if (element.hasClass("row-checkbox")) {
                    return;
                }
                while (!element.hasClass("book-main-row")) {
                    element = element.parent();
                }
                element.next().toggle();
            },
            startAddingBooks() {
                this.$router.push({name: 'add-book'});
            },
            editBook(event) {
                let bookId = event.target.dataset.id;
                this.$router.push({name: 'edit-book', params: {id: bookId}});
            },
            warnDeletingBooks(event) {
                let bookId = event.target.dataset.id;
                this.deletion.id_list = [];
                let vm = this;
                if (typeof bookId === 'undefined') {
                    $(".row-checkbox").each((i, element) => {
                        element = $(element);
                        if (element.prop("checked")) {
                            vm.deletion.id_list.push(element.data("id"));
                        }
                    });
                } else {
                    this.deletion.id_list = [bookId];
                }
                if (this.deletion.id_list.length === 0) {
                    return;
                }
                this.deletion.error = null;
                $("#delete-warning-modal").modal("show");
            },
            changePage(event) {
                let page = event.target.dataset.page;
                if (page === "prev" && this.pager.current !== 1) {
                    this.pager.current--;
                } else if (page === "next" && this.pager.current !== this.pager.total) {
                    this.pager.current++;
                } else {
                    this.pager.current = Number.parseInt(page);
                }
                this.loadBooks();
            },
            deleteBooks(event) {
                let vm = this;
                fetch("/api/librarian/book", {
                    method: "DELETE",
                    headers: {
                        "content-type": "application/json"
                    },
                    body: JSON.stringify({
                        id_list: vm.deletion.id_list
                    }),
                    credentials: "same-origin"
                }).then(resp => {
                    if (!resp.ok) {
                        throw new Error(resp.message);
                    }
                    vm.loadBooks();
                    $("#delete-warning-modal").modal("hide");
                }).catch(err => {
                    this.$emit("error", {error: err});
                });
                /*
                $.ajax(
                    {
                        url: "/api/librarian/book",
                        type: "DELETE",
                        dataType: "json",
                        contentType: "application/json",
                        data: JSON.stringify({
                            id_list: vm.deletion.id_list
                        }),
                        success: function (resp) {
                            vm.loadBooks();
                            $("#delete-warning-modal").modal("hide");
                        },
                        error: function (xhr) {
                            let resp = JSON.parse(xhr.responseText);
                            vm.deletion.error = resp.message;
                        }
                    }
                );
                */
            }
        },
        mounted() {
            this.loadBooks();
        },
        data: function () {
            return {
                search: {
                    type: 'title',
                    query: ''
                },
                books: [],
                deletion: {
                    error: null,
                    id_list: []
                },
                pager: {
                    current: 1,
                    total: 1,
                    count: 0
                }
            };
        },
        components: {}
    };
</script>
<style>
</style>