import React from 'react';
import ReactDOM from 'react-dom';

class TableRow extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        return (
            <tr>
                <td>{this.props.id}</td>
                <td>{this.props.user.id}</td>
                <td>{this.props.user.username}</td>
                <td>{this.props.book.id}</td>
                <td>{this.props.book.title}</td>
                <td>{this.props.startTime}</td>
                <td>{this.props.endTime}</td>
            </tr>
        );
    }
}

class Table extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        return (
            <table className="table">
                <thead>
                <tr>
                    <th>#</th>
                    <th>User ID</th>
                    <th>Username</th>
                    <th>Book ID</th>
                    <th>Book title</th>
                    <th>Start time</th>
                    <th>End time</th>
                </tr>
                </thead>
                <tbody>
                {this.props.rows.map(row => {
                    return <TableRow {...row} />
                })}
                </tbody>
            </table>
        );
    }
}

export {Table, TableRow};