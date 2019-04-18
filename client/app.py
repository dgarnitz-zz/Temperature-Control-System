from flask import Flask, render_template, request, redirect, flash, url_for, logging, session
from datetime import datetime
import requests
import json

app = Flask(__name__)

user = "username"
password = "password"
rooms = []
currentUpdate = {}


@app.route('/', methods=['GET', 'POST'])
def login():
    if request.method == 'POST': 
        username = request.form['username'] 
        password_candidate = request.form['password']
        credentials = {'useranme': username, 'password': password_candidate}
        # response = requests.post(
        #  'http://localhost:8080/login', data = credentials)

        # res = "true"
        # if response.text == 'true':
        if username == user and password == password_candidate:
            session['logged_in'] = True
            session['username'] = username
            flash('Login Successful', 'success')
            render_template('current.html')
        else:
            error = "Invalid username or password"
            return render_template('login.html', error=error)

    return render_template('login.html')


@app.route('/current')
def current():
    global currentUpdate
    try:
        r = requests.get('http://localhost:8080/history/current')
        object = json.loads(r.text)
        currentUpdate = object
        return render_template('current.html', rooms=[currentUpdate])
    except requests.exceptions.ConnectionError:
        flash("Could not connect to server", 'danger')
        return render_template('current.html')



@app.route('/upload', methods=['post'])
def upload():
    lower = request.form["lower"]
    upper = request.form["upper"]
    currentUpdate["lower"] = lower
    currentUpdate["upper"] = upper

    try:
        r = requests.post('http://localhost:8080/update/changetemp', data=json.dumps(
            currentUpdate), headers={'Content-type': 'application/json'})

        if (r.status_code != 200):
            flash("Bad response from server, status code {}.  Try again.".format(r.status_code), 'danger')
            return render_template('current.html')
        else:
            newUpdate = json.loads(r.text)
            return render_template('current.html', rooms=[newUpdate])
    except requests.exceptions.ConnectionError:
        flash("Could not connect to server", 'danger')
        return render_template('current.html')



@app.route('/history')
def history():
    global rooms
    try:
        r = requests.get('http://localhost:8080/history/rooms')
        print(r.text)
        rooms = json.loads(r.text)
        return render_template('history.html', rooms=rooms)

    except requests.exceptions.ConnectionError:
        flash("Could not connect to server", 'danger')
        return render_template('history.html')



@app.route("/viewhistory", methods=["post"])
def view_history():
    room_id = request.form["option"]
    print(room_id)

    try:
        r = requests.get('http://localhost:8080/history/view{}'.format(room_id))

        print(r.text)
        room = json.loads(r.text)

        return render_template('history.html', rooms=rooms, lab=room)

    except requests.exceptions.ConnectionError:
        flash("Could not connect to server", 'danger')
        return render_template('history.html')


@app.route('/logout')
def logout():
    session.clear()
    flash("Logged Out", 'success')
    return redirect(url_for('login'))


if __name__ == '__main__':
    app.secret_key = "secret"
    app.run(debug=True)
