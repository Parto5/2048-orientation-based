from werkzeug.security import generate_password_hash, check_password_hash
from flask import Flask, request, jsonify
from flask_sqlalchemy import SQLAlchemy


app = Flask(__name__)

# Konfiguracja bazy danych
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///new_game.db'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
db = SQLAlchemy(app)

# Definicja tabeli w bazie danych
class Score(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(100), unique=True, nullable=False)
    move_count = db.Column(db.Integer, default=0)
    password = db.Column(db.String(200), nullable=False)  # Hasło

# Inicjalizacja bazy danych (raz na początku)
with app.app_context():
    db.create_all()

# Endpoint do pobierania liczby ruchów
@app.route('/get_move_count', methods=['GET'])
def get_move_count():
    username = request.args.get('username')
    user = Score.query.filter_by(username=username).first()

    if not user:
        # Jeśli użytkownik nie istnieje, tworzymy go
        user = Score(username=username, move_count=0,  password="123")
        db.session.add(user)
        db.session.commit()

    return jsonify({"username": user.username, "move_count": user.move_count}), 200


# Endpoint do aktualizowania liczby ruchów
@app.route('/update_move_count', methods=['POST'])
def update_move_count():
    data = request.json
    username = data.get('username')
    move_count = data.get('move_count')

    user = Score.query.filter_by(username=username).first()

    if user:
        user.move_count = move_count
        db.session.commit()
        return jsonify({"message": "Move count updated", "move_count": user.move_count}), 200
    else:
        new_user = Score(username=username, move_count=move_count)
        db.session.add(new_user)
        db.session.commit()
        return jsonify({"message": "New user created", "move_count": new_user.move_count}), 201

# Endpoint rejestracji użytkownika
@app.route('/register', methods=['POST'])
def register_user():
    data = request.json
    username = data.get('username')
    password = data.get('password')

    # Sprawdzanie czy użytkownik już istnieje
    existing_user = Score.query.filter_by(username=username).first()
    if existing_user:
        return jsonify({"message": "User already exists"}), 400

    hashed_password = generate_password_hash(password, method='sha256')

    # Dodanie użytkownika do bazy
    new_user = Score(username=username, move_count=0, password=hashed_password)
    db.session.add(new_user)
    db.session.commit()

    return jsonify({"message": "User registered successfully"}), 201

# Endpoint logowania
@app.route('/login', methods=['POST'])
def login_user():
    data = request.json
    username = data.get('username')
    password = data.get('password')

    user = Score.query.filter_by(username=username).first()

    if not user:
        return jsonify({"message": "User not found"}), 404

    # Weryfikacja hasła
    if check_password_hash(user.password, password):
        return jsonify({"message": "Login successful", "move_count": user.move_count}), 200
    else:
        return jsonify({"message": "Invalid password"}), 401

@app.route('/check_db', methods=['GET'])
def check_db():
    try:
        # Sprawdzenie połączenia z bazą danych
        db.session.execute('SELECT 1')  # Próba wykonania prostego zapytania
        return jsonify({"status": "success", "message": "Database is available!"}), 200
    except Exception as e:
        return jsonify({"status": "error", "message": f"Failed to connect to database: {str(e)}"}), 500


if __name__ == '__main__':
    app.run(debug=True)
