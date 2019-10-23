<template>
    <div class="users-view">
        <h1 class="module-title">User management</h1>
        <div class="action-buttons">
            <button class="btn btn-success" data-toggle="modal" data-target="#add-user-modal">Add</button>
            <button class="btn btn-danger" @click="warnDeletingUsers">Delete selected users</button>
        </div>
        <div class="modal fade" tabIndex="-1" role="dialog" id="add-user-modal">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Register a new reader</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true" v-html="'&times;'"></span>
                        </button>
                    </div>
                    <div class="modal-body">
                        <form>
                            <div class="form-group">
                                <label for="add-user-modal-username">Username</label>
                                <input type="text" name="username" id="add-user-modal-username" class="form-control"
                                       required v-model="add_user.username">
                            </div>
                            <div class="form-group">
                                <label for="add-user-modal-password">
                                    Password
                                    <span style="font-size: 75%;">(Leave this blank to use the default password)</span>
                                </label>
                                <input type="text" name="password" id="add-user-modal-password" class="form-control"
                                       v-model="add_user.password">
                            </div>
                            <div class="alert alert-danger" v-if="add_user.error!==null">{{add_user.error}}</div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button class="btn btn-primary" @click="addUser">OK</button>
                        <button class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                    </div>
                </div>
            </div>
        </div>
        <div class="modal fade" tabIndex="-1" role="dialog" id="edit-user-modal">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Edit a reader</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true" v-html="'&times;'"></span>
                        </button>
                    </div>
                    <div class="modal-body">
                        <form>
                            <div class="form-group">
                                <label for="edit-user-modal-username">Username</label>
                                <input type="text" name="username" id="edit-user-modal-username" class="form-control"
                                       required v-model="edit_user.username">
                            </div>
                            <div class="form-group">
                                <label for="edit-user-modal-password">
                                    Password
                                </label>
                                <input type="text" name="password" id="edit-user-modal-password" class="form-control"
                                       v-model="edit_user.password">
                            </div>
                            <div class="alert alert-danger" v-if="edit_user.error!==null">{{edit_user.error}}</div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button class="btn btn-primary" @click="editUser">OK</button>
                        <button class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                    </div>
                </div>
            </div>
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
                            You are deleting {{deletion.id_list.length}} user{{deletion.id_list.length > 1 ?'s':''}}.
                            Are you sure?
                        </div>
                        <div class="alert alert-danger" v-if="deletion.error!==null">{{deletion.error}}</div>
                    </div>
                    <div class="modal-footer">
                        <button class="btn btn-primary" @click="deleteUsers">Confirm</button>
                        <button class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                    </div>
                </div>
            </div>
        </div>
        <div class="search-box">
            <form action="#" @submit="(e)=>{e.preventDefault();this.loadUsers();}">
                <div style="display: flex; flex-direction: row;">
                    <div style="width: 90%;" class="input-container input-group">
                        <input type="text" name="query" class="form-control" v-model="query"/>
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
                <th>Username</th>
                <th>Actions</th>
            </tr>
            </thead>
            <tbody>
            <template v-for="user in users">
                <tr v-bind:key="user.id" class="user-main-row">
                    <td>
                        <input type="checkbox" v-bind:data-id="user.id" class="row-checkbox"/>
                    </td>
                    <td>{{user.id}}</td>
                    <td>
                        <a href="javascript:void(0);" @click="viewUserHistory" v-bind:data-id="user.id">
                            {{user.username}}
                        </a>
                    </td>
                    <td>
                        <a href="javascript:void(0);" v-bind:data-id="user.id" v-bind:data-username="user.username"
                           @click="startEditingUser">Edit</a>
                        <a href="javascript:void(0);" @click="warnDeletingUsers" v-bind:data-id="user.id">Delete</a>
                    </td>
                </tr>
            </template>
            </tbody>
        </table>
        <div class="pager-container" v-if="pager.total > 0">
            <nav aria-label="Page selector">
                <ul class="pagination">
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
    export default {
        methods: {
            loadUsers() {
                let vm = this;
                $.ajax(
                    {
                        url: "/api/librarian/user/search",
                        type: "GET",
                        dataType: "json",
                        data: {
                            type: "username",
                            query: vm.query,
                            page: vm.pager.current
                        },
                        success: function (resp) {
                            vm.users = resp.data.users;
                            vm.pager.total = resp.data.total;
                            vm.pager.count = resp.data.count;
                        }
                    }
                );
            },
            addUser() {
                let vm = this;
                $.ajax(
                    {
                        url: "/api/librarian/user",
                        type: "POST",
                        dataType: "json",
                        data: {
                            username: vm.add_user.username,
                            password: vm.add_user.password
                        },
                        success: function (resp) {
                            vm.loadUsers();
                            $("#add-user-modal").modal("hide");
                            vm.add_user.username = '';
                            vm.add_user.password = '';
                        },
                        error: function (xhr) {
                            let resp = JSON.parse(xhr.responseText);
                            vm.add_user.error = resp.message;
                        }
                    }
                );
            },
            warnDeletingUsers(event) {
                let userId = event.target.dataset.id;
                this.deletion.id_list = [];
                let vm = this;
                if (typeof userId === 'undefined') {
                    $(".row-checkbox").each((i, element) => {
                        element = $(element);
                        if (element.prop("checked")) {
                            vm.deletion.id_list.push(element.data("id"));
                        }
                    });
                } else {
                    this.deletion.id_list = [userId];
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
                window.scrollTo(0, 0);
                this.loadUsers();
            },
            startEditingUser(event) {
                let id = event.target.dataset.id;
                let username = event.target.dataset.username;
                this.edit_user.username = username;
                this.edit_user.user_id = id;
                $("#edit-user-modal").modal();
            },
            editUser() {
                let vm = this;
                $.ajax(
                    {
                        url: "/api/librarian/user/" + vm.edit_user.user_id,
                        type: "PUT",
                        dataType: "json",
                        contentType: "application/json",
                        data: JSON.stringify({
                            username: vm.edit_user.username,
                            password: vm.edit_user.password
                        }),
                        success: function (resp) {
                            vm.loadUsers();
                            $("#edit-user-modal").modal("hide");
                        },
                        error: function (xhr) {
                            let resp = JSON.parse(xhr.responseText);
                            vm.edit_user.error = resp.message;
                        }
                    }
                );
            },
            deleteUsers: async function (event) {
                for (let id of this.deletion.id_list) {
                    let resp = await fetch("/api/librarian/user/" + id,
                        {
                            method: "DELETE",
                            credentials: "same-origin",
                        }
                    );
                    if (resp.ok) {
                        this.loadUsers();
                    } else {
                        let resp = JSON.parse(xhr.responseText);
                        this.deletion.error = resp.message;
                    }
                    $("#delete-warning-modal").modal("hide");
                }
            },
            viewUserHistory(event) {
                this.$router.push({name: 'user-history', params: {id: event.target.dataset.id}});
            }
        },
        mounted() {
            this.loadUsers();
        },
        data: function () {
            return {
                query: '',
                users: [],
                add_user: {
                    error: null,
                    username: '',
                    password: ''
                },
                edit_user: {
                    error: null,
                    user_id: null,
                    username: '',
                    password: ''
                },
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