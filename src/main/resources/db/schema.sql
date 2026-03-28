PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS groups (
group_id      INTEGER PRIMARY KEY AUTOINCREMENT,
name          TEXT NOT NULL,
is_temporary  INTEGER NOT NULL DEFAULT 0 CHECK (is_temporary IN (0, 1)),
created_at    TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS players (
player_id      INTEGER PRIMARY KEY AUTOINCREMENT,
group_id       INTEGER NOT NULL,
display_name   TEXT NOT NULL,
FOREIGN KEY (group_id) REFERENCES groups(group_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS games (
game_id        INTEGER PRIMARY KEY AUTOINCREMENT,
group_id       INTEGER NOT NULL,
status         TEXT NOT NULL CHECK (status IN ('IN_PROGRESS', 'FINISHED')),
rule_version   TEXT,
FOREIGN KEY (group_id) REFERENCES groups(group_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS players_in_game (
player_in_game_id  INTEGER PRIMARY KEY AUTOINCREMENT,
game_id            INTEGER NOT NULL,
player_id          INTEGER NOT NULL,
turn_order         INTEGER NOT NULL,
FOREIGN KEY (game_id) REFERENCES games(game_id) ON DELETE CASCADE,
FOREIGN KEY (player_id) REFERENCES players(player_id) ON DELETE RESTRICT,
UNIQUE (game_id, player_id),
UNIQUE (game_id, turn_order)
);

CREATE TABLE IF NOT EXISTS scores (
score_id         INTEGER PRIMARY KEY AUTOINCREMENT,
player_in_game_id   INTEGER NOT NULL,
turn_number      INTEGER NOT NULL,
score            INTEGER NOT NULL,
FOREIGN KEY (player_in_game_id) REFERENCES players_in_game(player_in_game_id) ON DELETE CASCADE,
UNIQUE (player_in_game_id, turn_number)
);

CREATE INDEX IF NOT EXISTS idx_players_group_id
    ON players(group_id);

CREATE INDEX IF NOT EXISTS idx_games_group_id
    ON games(group_id);

CREATE INDEX IF NOT EXISTS idx_players_in_game_game_id
    ON players_in_game(game_id);

CREATE INDEX IF NOT EXISTS idx_players_in_game_player_id
    ON players_in_game(player_id);

CREATE INDEX IF NOT EXISTS idx_scores_player_in_game_id
    ON scores(player_in_game_id);