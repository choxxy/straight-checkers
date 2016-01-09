package com.pennywise.checkers.screens.dialogs;

import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.pennywise.Assets;
import com.pennywise.checkers.core.Constants;

/**
 * Created by CHOXXY on 11/26/2015.
 */
public class GameDialog extends Dialog {

    public GameDialog(String title) {
        super(title, Assets.getSkin());
        initialize();
    }

    private void initialize() {
        padTop(60); // set padding on top of the dialog title
        setModal(true);
        setMovable(false);
        setResizable(false);
        getContentTable().defaults().expandX();
    }


    @Override
    public GameDialog text(String text) {
        super.text(new Label(text, Assets.getSkin()));
        return this;
    }

    /**
     * Adds a text button to the button table.
     *
     * @param listener the input listener that will be attached to the button.
     */
    public GameDialog button(String buttonText, InputListener listener) {
        TextButton button = new TextButton(buttonText, Assets.getSkin());
        button.addListener(listener);
        button(button);
        return this;
    }

    /**
     * Adds a text button to the button table.
     *
     * @param listener the input listener that will be attached to the button.
     */
    public GameDialog content(String buttonText, InputListener listener) {
        TextButton button = new TextButton(buttonText, Assets.getSkin());
        button.addListener(listener);
        getContentTable().add(button).height(60).fill().left().top().center();
        getContentTable().row();
        return this;
    }

    public GameDialog selectBox(String buttonText, ChangeListener listener) {
        getContentTable().row();
        SelectBox selectBox = new SelectBox(Assets.getSkin());
        selectBox.addListener(listener);
        selectBox.setItems("Host Game", "Connect to host");
        getContentTable().add(selectBox).height(60).fill().left().top().center();
        getContentTable().row();
        return this;
    }

    public GameDialog list(String buttonText, ClickListener listener) {

        Object[] listEntries = {"This is a list entry1", "And another one1", "The meaning of life1"};

        List list = new List(Assets.getSkin());
        list.setItems(listEntries);
        list.getSelection().setMultiple(false);
        list.getSelection().setRequired(false);
        list.addListener(listener);
        getContentTable().add(list).height(90).fill().left().top().center();
        getContentTable().row();
        return this;
    }

    @Override
    public float getPrefWidth() {
        // force dialog width
        return (Constants.GAME_WIDTH * 0.85f);
    }

    @Override
    public float getPrefHeight() {
        // force dialog height
        return (Constants.GAME_HEIGHT * 0.50f);
    }


}
