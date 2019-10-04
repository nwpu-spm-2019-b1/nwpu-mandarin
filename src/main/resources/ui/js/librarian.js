import React from 'react';
import ReactDOM from 'react-dom';
import {createStore} from 'redux';
import {Provider} from 'react-redux'
import {BrowserRouter as Router, Switch, Route, Link, withRouter} from "react-router-dom";
import {Table, ajax, Modal} from './common';

let store = createStore();

class BooksView extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            rows: []
        };
        this.type_options = React.createRef();
        this.query_input = React.createRef();
    }

    updateData = () => {
        ajax({
            url: "/api/librarian/book/search",
            type: "GET",
            dataType: "json",
            data: {
                type: this.type_options.current.value,
                query: this.query_input.current.value
            }
        }).then(resp => {
            this.setState({rows: resp.data});
        });
    };

    render() {
        return (
            <div className="books-view">
                <h2>Book management</h2>
                <div className="book-search-box">
                    <div style={{display: 'flex', flexDirection: 'row'}}>
                        <div style={{'width': '10%'}} className="input-container">
                            <select name="type" ref={this.type_options} className="form-control">
                                <option value="isbn">ISBN</option>
                                <option value="title">Title</option>
                                <option value="author">Author</option>
                                <option value="description">Description</option>
                            </select>
                        </div>
                        <div style={{'width': '80%'}} className="input-container">
                            <input type="text" name="query" ref={this.query_input} className="form-control"/>
                        </div>
                        <div style={{'width': '10%'}} className="input-container">
                            <button type="submit" className="btn btn-primary search-btn btn-block"
                                    onClick={this.updateData}>
                                Search
                            </button>
                        </div>
                    </div>
                </div>
                <table className="table">
                    <thead>
                    <tr>
                        <th>#</th>
                        <th>ISBN</th>
                        <th>Title</th>
                        <th>Author(s)</th>
                        <th>Description</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    {
                        this.state.rows.map(row => {
                            return (
                                <tr key={row.id}>
                                    <td>{row.id}</td>
                                    <td>{row.isbn}</td>
                                    <td>{row.title}</td>
                                    <td>{row.author}</td>
                                    <td>{row.description}</td>
                                    <td>
                                        <a href="javascript:void(0);" onClick={this.doEdit}>Edit</a>
                                        <a href="javascript:void(0);" onClick={this.doDelete}>Delete</a>
                                    </td>
                                </tr>
                            );
                        })
                    }
                    </tbody>
                </table>
            </div>
        );
    }
}

class Sidebar extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        let location = this.props.location.pathname;
        let items = [
            {path: "/", name: "Dashboard"},
            {path: "/books", name: "Manage books"},
            {path: "/users", name: "Manage users"}
        ];
        return (
            <div className="col-2 col-xl-1 sidebar">
                <nav className="sidebar-nav">
                    <ul>
                        {
                            items.map(item => {
                                return (
                                    <li className={location === item.path ? 'active' : ''}>
                                        <Link to={item.path}>{item.name}</Link>
                                    </li>
                                );
                            })
                        }
                    </ul>
                </nav>
            </div>
        );
    }
}

Sidebar = withRouter(Sidebar);

class LibrarianView extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        return (
            <Provider store={store}>
                <Router basename="/librarian">
                    <div className="container-fluid">
                        <div className="row">
                            <Sidebar/>
                            <div className="col main-column">
                                <div className="p-3">
                                    <Switch>
                                        <Route exact path="/">
                                            <div>Front page</div>
                                        </Route>
                                        <Route path="/books">
                                            <BooksView/>
                                        </Route>
                                        <Route path="/users">
                                            <div>UserView placeholder</div>
                                        </Route>
                                    </Switch>
                                </div>
                            </div>
                        </div>
                    </div>
                </Router>
            </Provider>
        );
    }
}

class BookEditModal extends React.Component {
    constructor(props) {
        super(props);
        this.modal = React.createRef();
    }

    onSubmit = (event) => {
        event.preventDefault();
        let form = event.relatedTarget;
        let isbn = form.querySelector("input[name=isbn]")[0].value;
        let title = form.querySelector("input[name=title]")[0].value;
        alert(isbn, title);
    };

    renderBody() {
        return (
            <form onSubmit={this.props.onSubmit}>
                <div className="form-group">
                    <label for="isbn-input">ISBN</label>
                    <input className="form-control" id="isbn-input" name="isbn"/>
                </div>
                <div className="form-group">
                    <label for="title-input">Title</label>
                    <input className="form-control" id="title-input" name="title"/>
                </div>
            </form>
        );
    }

    render() {
        return <Modal ref={this.modal}
                      id={this.props.new ? 'add-book-modal' : 'edit-book-modal'}
                      title={this.props.new ? 'Add books' : 'Edit book'}
                      body={this.renderBody()}/>;
    }

}

ReactDOM.render(<LibrarianView/>, document.getElementById("app"));