package com.picklegames.gameStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.picklegames.TweenAccessor.SpriteTweenAccessor;
import com.picklegames.game.FireFighterGame;
import com.picklegames.managers.GameStateManager;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;

public class Menu extends GameState {
	private Sprite background, whitebg;
	private TextBox[] textBoxes;
	private int index = 0;
	private BitmapFont font;

	public Menu(GameStateManager gsm) {
		super(gsm);

	}

	@Override
	public void init() {
		Tween.registerAccessor(Sprite.class, new SpriteTweenAccessor());
		FireFighterGame.res.loadTexture("image/Backgrounds/Menu.png", "background");
		FireFighterGame.res.loadTexture("image/Backgrounds/whitebg.png", "whitebg");

		whitebg = new Sprite(FireFighterGame.res.getTexture("whitebg"));
		whitebg.setSize(cam.viewportWidth, cam.viewportHeight);
		whitebg.setAlpha(0);
		background = new Sprite(FireFighterGame.res.getTexture("background"));
		background.setSize(cam.viewportWidth, cam.viewportHeight);

		textBoxes = new TextBox[3];
		textBoxes[0] = new TextBox("Play", cam.viewportWidth - 325, 500);
		textBoxes[1] = new TextBox("Help", cam.viewportWidth - 325, 440);
		textBoxes[2] = new TextBox("Exit", cam.viewportWidth - 325, 380);

		FireFighterGame.res.loadMusic("sound/Menu, Dialogue 2.mp3", "men");
		FireFighterGame.res.getMusic("men").setLooping(true);
		FireFighterGame.res.getMusic("men").play();
		
		font = new BitmapFont(Gdx.files.internal("font/comicsan.fnt"));
		font.getData().setScale(1.5f);
	}

	@Override
	public void handleInput() {
		if (Gdx.input.isKeyJustPressed(Keys.DOWN)) {
			textBoxes[index].setHighLight(false);
			if (index >= 2)
				index = 0;
			else {
				index++;
			}
		}
		if (Gdx.input.isKeyJustPressed(Keys.UP)) {
			textBoxes[index].setHighLight(false);
			if (index <= 0)
				index = 2;
			else {
				index--;
			}
		}
		if (Gdx.input.isKeyJustPressed(Keys.ENTER)) {
			textBoxes[index].setActivated(true);
		}

		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE) && isHelp) {
			isHelp = false;
		}

	}

	float timeElapsed;
	boolean isTween = false;
	boolean isHelp = false;

	@Override
	public void update(float dt) {
		handleInput();

		textBoxes[index].setHighLight(true);
		for (TextBox t : textBoxes) {
			t.update(dt);
			if (t.isActivated()) {
				if (t.text.equals("Play")) {
					if (!isTween) {
						Tween.to(whitebg, SpriteTweenAccessor.ALPHA, 2f).target(1).ease(TweenEquations.easeNone)
								.start(gsm.getTweenManager());
						isTween = true;
					}
					//System.out.println(whitebg.getColor().a);
					timeElapsed += dt;
					if (timeElapsed >= 3f) {
						FireFighterGame.res.getMusic("men").stop();
						gsm.setState(GameStateManager.PLAY);
					}
				} else if (t.text.equals("Help")) {
					isHelp = true;
					t.isActivated = false;
					System.out.println("lol");
				} else if (t.text.equals("Exit")) {
					System.exit(0);
				}
			}
			
		}
		
		System.out.println(isHelp);

	}

	@Override
	public void render() {
		batch.begin();
		background.draw(batch);

		if (isHelp) {
			background.draw(batch);
			font.setColor(Color.WHITE);
			font.draw(batch, "[1][2] to switch weapon\n" + "[SPACE] to use weapon\n" + "[X] to interact\n" + "\n" + "",
					cam.viewportWidth / 2, 500);
			font.setColor(Color.YELLOW);
			font.draw(batch, "Escape to go back", 0, 100);

		} else {
			for (TextBox t : textBoxes) {
				t.render();
			}
		}
		whitebg.draw(batch);
		batch.end();

	}

	@Override
	public void dispose() {
		whitebg.getTexture().dispose();
	}

	public class TextBox {
		private String text;
		private Vector2 pos;
		private BitmapFont font;
		private boolean isHighLight;
		private boolean isActivated;

		public TextBox(String text, float x, float y) {
			this.text = text;
			pos = new Vector2(x, y);
			font = new BitmapFont(Gdx.files.internal("font/comicsan.fnt"));
			font.getData().setScale(1.5f);
		}

		public void update(float dt) {
			if (isHighLight) {
				font.setColor(Color.YELLOW);
			} else {
				font.setColor(Color.WHITE);
			}
		}

		public void render() {
			font.draw(batch, text, pos.x, pos.y);
		}

		public void dispose() {
			font.dispose();
		}

		public void setHighLight(boolean b) {
			isHighLight = b;
		}

		public boolean isActivated() {
			return isActivated;
		}

		public void setActivated(boolean isActivated) {
			this.isActivated = isActivated;
		}

		public String getText() {
			return text;
		}
	}

}
