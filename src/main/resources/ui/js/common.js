import React from 'react';
import $ from 'jquery';

function ajax(o) {
    return new Promise((resolve, reject) => {
        o.success = function (resp) {
            resolve(resp);
        };
        o.error = function (xhr) {
            reject(xhr)
        };
        $.ajax(o);
    });
}

class Modal extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        return (
            <div className="modal fade" tabIndex="-1" role="dialog" id={this.props.id}>
                <div className="modal-dialog" role="document">
                    <div className="modal-content">
                        <div className="modal-header">
                            <h5 className="modal-title">{this.props.title}</h5>
                            <button type="button" className="close" data-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <div className="modal-body">
                            {this.props.body}
                        </div>
                        <div className="modal-footer">
                            <button type="button" className="btn btn-primary" onClick={this.onAccept}>
                                {this.props.accept}
                            </button>
                            <button type="button" className="btn btn-secondary" data-dismiss="modal">
                                {this.props.reject}
                            </button>
                        </div>
                    </div>
                </div>
            </div>
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
                    {
                        this.props.columns.map(o => {
                            return <th>{o.name}</th>;
                        })
                    }
                </tr>
                </thead>
                <tbody>
                {
                    this.props.rows.map(row => {
                        return (
                            <tr key={row[this.props.keyName]}>
                                {
                                    this.props.columns.map(o => {
                                        return <td>{row[o.key]}</td>;
                                    })
                                }
                            </tr>
                        );
                    })
                }
                </tbody>
            </table>
        );
    }
}

export {Table, ajax};