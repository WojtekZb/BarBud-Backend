CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(150) NOT NULL UNIQUE,
    username VARCHAR(20) NOT NULL,
    password_hash TEXT NOT NULL,
    refresh_token TEXT,
    refresh_token_expiry TIMESTAMP
);

CREATE TABLE bars (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    name VARCHAR(150) NOT NULL,

    CONSTRAINT fk_bars_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

CREATE TABLE ingredients (
    id SERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL UNIQUE,
    category VARCHAR(100)
);

CREATE TABLE drinks (
    id SERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL UNIQUE,
    description TEXT,
    steps TEXT,
    alcohol_percentage DECIMAL(5,2)
);

CREATE TABLE drink_ingredients (
    drink_id INT NOT NULL,
    ingredient_id INT NOT NULL,
    amount DECIMAL(10,2),
    unit VARCHAR(50),

    PRIMARY KEY (drink_id, ingredient_id),

    CONSTRAINT fk_drink_ingredients_drink
        FOREIGN KEY (drink_id)
        REFERENCES drinks(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_drink_ingredients_ingredient
        FOREIGN KEY (ingredient_id)
        REFERENCES ingredients(id)
        ON DELETE CASCADE
);

CREATE TABLE bar_ingredients (
    bar_id INT NOT NULL,
    ingredient_id INT NOT NULL,

    PRIMARY KEY (bar_id, ingredient_id),

    CONSTRAINT fk_bar_ingredients_bar
        FOREIGN KEY (bar_id)
        REFERENCES bars(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_bar_ingredients_ingredient
        FOREIGN KEY (ingredient_id)
        REFERENCES ingredients(id)
        ON DELETE CASCADE
);