package com.picklegames.levelStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.picklegames.TweenAccessor.SpriteTweenAccessor;
import com.picklegames.game.FireFighterGame;
import com.picklegames.handlers.Animation;
import com.picklegames.handlers.dialogue.Dialogue;
import com.picklegames.managers.LevelStateManager;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;

public class Level7 extends LevelState {

	// 4th dialogue with fire chief

	private Dialogue d;
	private BitmapFont font;
	private GlyphLayout layout;

	private Texture bg;
	private Texture bgBar;

	private Animation ani1;
	private TextureRegion[] reg1;

	private Animation ani2;
	private TextureRegion[] reg2;
	private Sprite white;
	private Sound playerS, girlS, catS, currentSound;

	public Level7(LevelStateManager lsm) {
		super(lsm);
		// TODO Auto-generated constructor stub

		init();
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		d = new Dialogue("dialogue/dialogue4.txt", "Autumn, 1995");
		font = new BitmapFont(Gdx.files.internal("font/comicsan.fnt"));
		font.setColor(Color.WHITE);
		font.getData().scaleX = .4f;
		layout = new GlyphLayout(); // dont do this every frame! Store it as
									// member

		white = new Sprite(new Texture(("image/Backgrounds/whitebg.png")));
		white.setAlpha(0);
		white.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		white.setPosition(0, 0);

		FireFighterGame.res.loadTexture("image/Character/miggyFace.png", "miggy");
		FireFighterGame.res.loadTexture("image/Character/collegeFace.png", "coll");
		FireFighterGame.res.loadTexture("image/Backgrounds/fStationN.png", "bg");
		FireFighterGame.res.loadTexture("image/Backgrounds/diaBar.png", "diaBox");

		reg1 = TextureRegion.split(FireFighterGame.res.getTexture("coll"), 300, 300)[0];
		ani1 = new Animation();
		ani1.setFrames(reg1, 16f);

		reg2 = TextureRegion.split(FireFighterGame.res.getTexture("miggy"), 300, 300)[0];
		ani2 = new Animation();
		ani2.setFrames(reg2, 8f);

		bg = FireFighterGame.res.getTexture("bg");
		bgBar = FireFighterGame.res.getTexture("diaBox");

		FireFighterGame.res.loadSound("sound/wac.mp3", "playerS");
		FireFighterGame.res.loadSound("sound/wac.mp3", "girlS");
		FireFighterGame.res.loadSound("sound/wac.mp3", "catS");
		playerS = FireFighterGame.res.getSound("playerS");
		girlS = FireFighterGame.res.getSound("girlS");
		catS = FireFighterGame.res.getSound("catS");
		currentSound = playerS;

		// cam.position.set(FireFighterGame.V_WIDTH / 2 ,
		// FireFighterGame.V_HEIGHT/ 2 , 0);
		cam.update();
		System.out.println(cam.position.toString());

		FireFighterGame.res.loadMusic("sound/Dialogue 4.mp3", "d_4");

	}

	@Override
	public void handleInput() {
		if (FireFighterGame.DEBUG) {
			if (Gdx.input.isKeyPressed(Keys.P)) {
				lsm.setState(LevelStateManager.Level_8);
			}
		}
	}

	boolean isTween;

	@Override
	public void update(float dt) {
		// TODO Auto-generated method stub
		handleInput();

		if (Gdx.input.isKeyPressed(Keys.P)) {
			FireFighterGame.res.getMusic("d_4").stop();
			lsm.setState(LevelStateManager.Level_8);
		}

		if (!FireFighterGame.res.getMusic("d_4").isPlaying()) {
			FireFighterGame.res.getMusic("d_4").play();
		}

		if (d.isFinished()) {
			if (!isTween) {
				Tween.to(white, SpriteTweenAccessor.ALPHA, 2f).target(1).ease(TweenEquations.easeNone)
						.start(lsm.getTweenManager());
				isTween = true;
			}
			if (white.getColor().a >= .95f) {
				FireFighterGame.res.getMusic("d_4").stop();
				lsm.setState(LevelStateManager.Level_8);
			}

		}

		if (d.getName().equals("YOU")) {
			currentSound = playerS;
		} else if (d.getName().equals("GIRL")) {
			currentSound = girlS;
		} else if (d.getName().equals("CAT")) {
			currentSound = catS;

		}

		d.update(dt, currentSound);

		if (d.isIntroDone()) {
			// teenGirl.update(dt);
			// teenAni.update(dt);

			if (d.getName().equals("YOU")) {
				ani1.setCurrentFrame(d.getCurrentLine().getAnimationIndex());
			} else if (d.getName().equals("CHIEF MIGGY")) {
				ani2.setCurrentFrame(d.getCurrentLine().getAnimationIndex());
			}
		}

		if (d.getName().equals("YOU")) {
			font.setColor(Color.BLUE);
		} else if (d.getName().equals("CHIEF MIGGY")) {
			font.setColor(Color.FIREBRICK);
		} else {
			font.setColor(Color.GREEN);
		}

	}

	@Override
	public void render() {
		// TODO Auto-generated method stub
		layout.setText(font, d.getCharacterLine());

		float width = layout.width;// contains the width of the current set text
		// float height = layout.height; // contains the height of the current
		// set text
		batch.setProjectionMatrix(cam.combined);

		batch.begin();

		batch.draw(bg, 0, 0, cam.viewportWidth, cam.viewportHeight);
		batch.draw(bgBar, 25, cam.viewportHeight - cam.viewportHeight / 4, cam.viewportWidth - 50,
				cam.viewportHeight / 6);
		batch.draw(ani1.getFrame(), 5, 5, 500, 500);
		batch.draw(ani2.getFrame(), cam.viewportWidth - 505, 5, 500, 500);

		font.draw(batch, d.getCharacterLine(), cam.viewportWidth / 2 - width / 2, 600);
		layout.setText(font, d.getName());
		width = layout.width;
		font.draw(batch, d.getName(), cam.viewportWidth / 2 - width / 2, 625);

		batch.end();

		d.render(batch);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		d.dispose();
		FireFighterGame.res.removeSound("playerS");
		FireFighterGame.res.removeSound("girldS");
		FireFighterGame.res.removeSound("catS");
		FireFighterGame.res.getMusic("d_4").dispose();
		;

	}

}
