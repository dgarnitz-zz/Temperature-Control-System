from flask import Flask, render_template, request, redirect, jsonify, make_response

from datetime import datetime

app = Flask(__name__)


@app.route('/')
def current():
    return render_template('current.html')


@app.route('/history')
def history():
    return render_template('history.html')


if __name__ == '__main__':
    app.run(debug=True)
