import hashlib
from flask import Flask, jsonify
from flask_restful import Resource, Api, reqparse
import db
import waitress
import sys
from flask_jwt_extended import create_access_token, jwt_required, JWTManager, get_jwt_identity
import os
from flask_cors import CORS
import datetime
from dotenv import load_dotenv

load_dotenv()

app = Flask(__name__)
app.config["JWT_SECRET_KEY"] = os.getenv('jwt_secret_key')
CORS(app)
jwt = JWTManager(app)
api = Api(app)

deviceParser = reqparse.RequestParser()
deviceParser.add_argument("id", type=int, required=True)
deviceParser.add_argument("name", type=str, required=True)
deviceParser.add_argument("type", type=str, required=True)
deviceParser.add_argument("network_ssid", type=str, required=True)
deviceParser.add_argument("ip", type=str, required=True)
deviceParser.add_argument("id_user", type=int, required=True)

class Device(Resource):
    @jwt_required()
    def get(self, id):
        pass

    @jwt_required()
    def post(self, id):
        try:
            args = deviceParser.parse_args()
            device = db.Device(name=args["name"], type=args["type"], network_ssid=args["network_ssid"], ip=args["ip"], id_user=args["id_user"])
            db.session.add(device)
            db.session.commit()
            return {"message": "Device created successfully"}, 200
        except:
            return {"message": "Invalid data"}, 400 

    @jwt_required()
    def delete(self, id):
        pass

    @jwt_required()
    def put(self, id):
        pass

class DeviceList(Resource):
    @jwt_required()
    def get(self):
        return list(map(lambda x: x.as_dict(), db.session.query(db.Device).filter(db.Device.id_user == get_jwt_identity()["id"]).all()))

authParser = reqparse.RequestParser()
authParser.add_argument("password", required=True)
authParser.add_argument("username", required=True)

class Auth(Resource):
    def post(self):
        try:
            args = authParser.parse_args()
        except Exception as e:
            return {"error": str(e)}, 342

        try:
            user = db.session.query(db.User).filter(
                db.User.username == args["username"]).one()
            key = hashlib.pbkdf2_hmac('sha256', args["password"].encode(
                "utf-8"), user.salt.encode("utf-8"), 100000).hex()
            if key == user.password:
                print(user.as_dict())
                return {"token": create_access_token(identity=user.as_dict(), expires_delta=False)}, 200
            else:
                return {"error": "Incorrect username or password"}, 342
        except Exception as e:
            return {"error": str(e)}, 342

    def put(self):
        try:
            args = authParser.parse_args()
        except Exception as e:
            return {"error": e.__str__()}, 342

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
api.add_resource(Device, "/v1/device/<int:id>")
api.add_resource(DeviceList, "/v1/device_list")

if __name__ == '__main__':
    if len(sys.argv) != 2:
        print("Usage: python3 main.py [dev | prod]")
        exit(1)
    if sys.argv[1] == "prod":
        waitress.serve(app, host="0.0.0.0", port="5000")
    else:
        app.run(host="0.0.0.0", debug=True, port=5000)
