import hashlib
from flask import Flask, send_from_directory
from flask_restful import Resource, Api, reqparse
import db
import waitress
import sys
from flask_jwt_extended import create_access_token, jwt_required, JWTManager
import os
import datetime
from dotenv import load_dotenv

load_dotenv()

app = Flask(__name__)
app.config["JWT_SECRET_KEY"] = os.getenv('jwt_secret_key')
jwt = JWTManager(app)
api = Api(app)

authParser = reqparse.RequestParser()
authParser.add_argument("password", required=True)
authParser.add_argument("username", required=True)


class Auth(Resource):
    def post(self):
        try:
            args = authParser.parse_args()
        except:
            return {"error": "Incorrect arguments provided"}, 342

        try:
            user = db.session.query(db.User).filter(
                db.User.username == args["username"]).one()
            key = hashlib.pbkdf2_hmac('sha256', args["password"].encode(
                "utf-8"), user.salt.encode("utf-8"), 100000).hex()
            if key == user.password:
                return {"token": create_access_token(identity=user.username, expires_delta=False)}, 200
            else:
                return {"error": "Incorrect username or password"}, 342
        except Exception as e:
            return {"error": str(e)}, 342

    def put(self):
        try:
            args = authParser.parse_args()
        except:
            return {"error": "Incorrect arguments provided"}, 342

        try:
            user = db.session.query(db.User).filter(
                db.User.username == args["username"]).first()
            if user is not None:
                return {"error": "Username already exists"}, 342
            salt=os.urandom(16).hex()
            key = hashlib.pbkdf2_hmac('sha256', args["password"].encode(
                "utf-8"), salt.encode("utf-8"), 100000).hex()
            user = db.User(username=args["username"], password=key, salt=salt)
            db.session.add(user)
            db.session.commit()
            return {"status": "OK"}, 200

           
        except Exception as e:
            return {"error": str(e)}, 342

api.add_resource(Auth, "/v1/auth")

if __name__ == '__main__':
    if len(sys.argv) != 2:
        print("Usage: python3 main.py [dev | prod]")
        exit(1)
    if sys.argv[1] == "prod":
        waitress.serve(app, host="0.0.0.0", port="5000")
    else:
        app.run(host="0.0.0.0", debug=True, port=5000)
