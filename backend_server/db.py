from sqlalchemy import Boolean, Float, ForeignKey, create_engine, Table, Date
from sqlalchemy import Column, String, INTEGER, DATE, BINARY
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker, relationship, registry

db_string = "sqlite:///test.db"

db = create_engine(db_string)
base: registry = declarative_base()


class Serial:
    def as_dict(self):
        return {c.name: str(getattr(self, c.name)) if getattr(self, c.name) else None for c in self.__table__.columns}


class User(base, Serial):
    __tablename__ = "User"
    id = Column(INTEGER, primary_key=True)
    username = Column(String, nullable=False, unique=True)
    password = Column(String, nullable=False)
    salt = Column(String, nullable=False)


Session = sessionmaker(db)
session = Session()


if __name__ == "__main__":
    base.metadata.create_all(db)
