package com.scoreboard.app.controller;

import com.scoreboard.app.AppContext;

public interface ContextAwareController {
    void setContext(AppContext context);
}
