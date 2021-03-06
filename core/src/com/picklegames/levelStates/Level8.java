package com.picklegames.levelStates;

import static com.picklegames.handlers.Box2D.B2DVars.PPM;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.picklegames.TweenAccessor.ParticleEffectTweenAccessor;
import com.picklegames.entities.Animal;
import com.picklegames.entities.Debris;
import com.picklegames.entities.Explosion;
import com.picklegames.entities.Fire;
import com.picklegames.entities.Lamp;
import com.picklegames.entities.Lamp.CharacterState;
import com.picklegames.entities.Lamp.WeaponState;
import com.picklegames.entities.Person;
import com.picklegames.entities.Person.PersonState;
import com.picklegames.entities.Transport;
import com.picklegames.entities.Animal.AnimalState;
import com.picklegames.entities.weapons.Axe;
import com.picklegames.entities.weapons.Extinguisher;
import com.picklegames.game.FireFighterGame;
import com.picklegames.handlers.CameraStyles;
import com.picklegames.handlers.HUD;
import com.picklegames.handlers.HUD.HudState;
import com.picklegames.handlers.TileObject;
import com.picklegames.handlers.Box2D.B2DVars;
import com.picklegames.handlers.Box2D.CreateBox2D;
import com.picklegames.managers.LevelStateManager;

import aurelienribon.tweenengine.Tween;

public class Level8 extends LevelState {

	private BitmapFont font;
	private OrthogonalTiledMapRenderer tmr;
	private TiledMap tileMap;
	private TileObject tileObject;

	private Box2DDebugRenderer b2dr;
	private Lamp player;
	private Transport transport;

	private ArrayList<Debris> crap;
	private ArrayList<Person> people;
	private ArrayList<Fire> fires;
	private ArrayList<Explosion> explosions;
	private ArrayList<Animal> animals;

	private CameraStyles camStyle;

	private HUD hud;

	public Level8(LevelStateManager lsm) {
		super(lsm);

	}

	@Override
	public void init() {

		Tween.registerAccessor(ParticleEffect.class, new ParticleEffectTweenAccessor());

		tileMap = new TmxMapLoader().load("map/vetlevel1.tmx");
		tmr = new OrthogonalTiledMapRenderer(tileMap);

		// cam.viewportWidth = tmr.getMap().getProperties().get("width",
		// Integer.class) * 32;
		// cam.viewportHeight = tmr.getMap().getProperties().get("height",
		// Integer.class) * 32;
		// cam.viewportWidth = tmr.getMap().getProperties().get("width",
		// Integer.class) * 32;
		cam.viewportHeight = tmr.getMap().getProperties().get("height", Integer.class) * 32;
		// cam.position.x = cam.viewportWidth / 2;
		cam.position.y = cam.viewportHeight / 2;

		// batch.setTransformMatrix(cam.combined.scl(PPM));

		player = lsm.getPlayer();

		player.characterState = CharacterState.ADULT;
		player.scl(1f);
		player.setBody(CreateBox2D.createBox(FireFighterGame.world, 100, 100, player.getWidth() / 3.5f,
				player.getHeight() / 9, new Vector2(0, -player.getHeight() / 2.5f), BodyType.DynamicBody, "lamp",
				B2DVars.BIT_PLAYER, B2DVars.BIT_GROUND));

		b2dr = new Box2DDebugRenderer();

		font = new BitmapFont();

		tileObject = new TileObject();
		tileObject.parseTiledObjectLayer(game.getWorld(), tileMap.getLayers().get("streetbound").getObjects(),
				"ground");

		crap = new ArrayList<Debris>();
		people = new ArrayList<Person>();
		fires = new ArrayList<Fire>();
		explosions = new ArrayList<Explosion>();
		animals = new ArrayList<Animal>();

		hud = new HUD(cam);

		createDebrisBox2D();
		camStyle = new CameraStyles();

		FireFighterGame.res.loadMusic("sound/actionMusic1.mp3", "l_3");

	}

	@Override
	public void handleInput() {
		// TODO Auto-generated method stub
		if (!player.getCurrentWeapon().isUse()) {
			if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
				player.setVelocityX(2);
			} else if (Gdx.input.isKeyPressed(Keys.LEFT)) {
				player.setVelocityX(-2);
			} else {
				player.setVelocityX(0);
			}
			if (Gdx.input.isKeyPressed(Keys.UP)) {
				player.setVelocityY(2);
			} else if (Gdx.input.isKeyPressed(Keys.DOWN)) {
				player.setVelocityY(-2);
			} else {
				player.setVelocityY(0);
			}

		}
		if (FireFighterGame.DEBUG) {
			if (Gdx.input.isKeyPressed(Keys.P)) {
				FireFighterGame.res.getMusic("l_3").stop();
				lsm.setState(LevelStateManager.Level_9);
			}
			if (Gdx.input.isKeyPressed(Keys.Q)) {
				cam.viewportHeight += 10;
				cam.viewportWidth += 10;
			} else if (Gdx.input.isKeyPressed(Keys.E)) {
				cam.viewportHeight -= 10;
				cam.viewportWidth -= 10;
			}
		}
		// if (player.getCurrentWeapon().isUsable()) {
		if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
			player.use();
		}
		// }

		if (player.characterState.equals(CharacterState.ADULT)) {
			if (Gdx.input.isKeyJustPressed(Keys.NUM_1)) {
				player.getCurrentWeapon().reset();
				player.weaponState = WeaponState.AXE;
			}
			if (Gdx.input.isKeyJustPressed(Keys.NUM_2)) {
				player.getCurrentWeapon().reset();
				player.weaponState = WeaponState.EXTINGUISHER;
			}
		}

		
	}

	private float timeElapsed = 0;
	boolean isTransport = false;

	@Override
	public void update(float dt) {

		if (!FireFighterGame.res.getMusic("l_3").isPlaying()) {
			FireFighterGame.res.getMusic("l_3").play();
		}

		handleInput();

		player.update(dt);
		// player.getBody().setLinearVelocity(player.getVelocity());
		// player.setSize(player.getWidth() - 2, player.getHeight() - 2);

		transport.update(dt);
		hud.update(dt);
		hud.playerHurt(player.isInDanger());
		if (player.isDead()) {
			lsm.setState(LevelStateManager.Dead);
		}

		if (player.weaponState.equals(WeaponState.AXE)) {

			hud.hudState = HudState.AXE;
		} else if (player.weaponState.equals(WeaponState.EXTINGUISHER)) {
			hud.hudState = HudState.EXTINGUISHER;
		}

		if (transport.isInRange(player.getWorldPosition().x, player.getWorldPosition().y, 100)) {
			isTransport = true;
		}
		if (isTransport) {
			timeElapsed += dt;
			if (!lsm.getTe().isStart()) {
				lsm.getTe().start();
			}
			if (timeElapsed >= 2f) {
				FireFighterGame.res.getMusic("l_3").stop();
				lsm.setState(LevelStateManager.Level_9);
			}
		} else {
			for (int i = 0; i < fires.size(); i++) {
				Fire f = fires.get(i);
				f.update(dt);

				if (f.isInRadius(player.getWorldPosition().x, player.getWorldPosition().y, 120)) {
					player.burn(1f);
				}

				if (!(player.getCurrentWeapon() instanceof Extinguisher))
					continue;

				if (player.getCurrentWeapon().isInRange(f.getPosition().x * PPM, f.getPosition().y * PPM)) {
					if (player.getCurrentWeapon().isUse()) {
						float life = f.getParticleEffect().getEmitters().first().getLife().getHighMax();
						f.getParticleEffect().getEmitters().first().getLife().setHighMax(life -= 5f);

						// Tween.to(f.getParticleEffect(),
						// ParticleEffectTweenAccessor.LIFE, 2).target(0, 0)
						// .ease(TweenEquations.easeNone).start(lsm.getTweenManager());
					} else {

					}

				}
				// System.out.println(f.getParticleEffect().getEmitters().first().getLife().getHighMax()
				// );
				if (f.getParticleEffect().getEmitters().first().getLife().getHighMax() <= 0f) {
					f.dispose();
					game.getWorld().destroyBody(f.getBody());
					fires.remove(f);
					i--;
				}
			}

			for (Person p : people) {
				p.update(dt);

				if (p.isInRadius(player.getPosition().x, player.getPosition().y, 2)) {
					p.personState = PersonState.RUN;
				}
			}

			for (Animal a : animals) {
				a.update(dt);

				if (a.isInRadius(player.getPosition().x, player.getPosition().y, 2)) {
					a.animalState = AnimalState.RUN;
				}
			}

			for (int i = 0; i < crap.size(); i++) {
				Debris d = crap.get(i);
				d.update(dt);
				if (!(player.getCurrentWeapon() instanceof Axe))
					continue;

				if (player.getCurrentWeapon().isInRange(d.getPosition().x * PPM, d.getPosition().y * PPM)) {
					if (player.getCurrentWeapon().isUse()) {
						if (!player.getCurrentWeapon().isUsable() && Gdx.input.isKeyJustPressed(Keys.J)) {
							d.doHit();
						}
					}

				}

				if (d.isBreakAnimationDone()) {
					d.dipose();
					game.getWorld().destroyBody(d.getBody());
					crap.remove(i);
					i--;
				}
			}

			for (Animal a : animals) {
				a.update(dt);

				if (a.isInRadius(player.getPosition().x, player.getPosition().y, 2)) {
					a.animalState = AnimalState.RUN;
				}
			}

			for (int i = 0; i < explosions.size(); i++) {
				Explosion e = explosions.get(i);
				e.update(dt);
				if (e.isInRadius(player.getPosition().x * PPM, player.getPosition().y * PPM, 300)) {
					e.push(player.getBody());
					e.start();

				}
				if (e.isStart()) {
					camStyle.Shake(cam, initialCamPos, 500f, 1f);
				}

				if (e.isComplete()) {
					e.dispose();
					game.getWorld().destroyBody(e.getBody());
					explosions.remove(e);
					i--;
				}
			}
		}

		camStyle.update(dt);
	}

	Vector3 initialCamPos = new Vector3(cam.position);

	@Override
	public void render() {
		// TODO Auto-generated method stub
		batch.setProjectionMatrix(cam.combined);

		float startx = cam.viewportWidth / 2;
		float starty = cam.viewportHeight / 2;
		float endWidth = tileMap.getProperties().get("width", Integer.class) * 32 - startx * 2;
		float endHeight = tileMap.getProperties().get("height", Integer.class) * 32 - starty * 2;
		System.out.println("endx: " + endWidth + " endy: " + endHeight);
		System.out.println(cam.position);

		camStyle.Lerp(cam, .5f, player.getWorldPosition());
		camStyle.Boundary(cam, startx, starty, endWidth, endHeight);
		initialCamPos = new Vector3(cam.position);
		// lsm.getCamStyle().Shake(cam, initialCamPos, 2f);

		tmr.setView(cam);
		batch.begin();
		tmr.render();
		// b2dr.render(game.getWorld(), cam.combined.scl(PPM));
		batch.end();

		cam.update();

		player.render(batch);

		hud.render(batch);

		batch.begin();
		transport.render(batch);

		for (Debris d : crap) {
			d.render(batch);
		}

		for (Person p : people) {
			p.render(batch);
		}

		for (Animal a : animals) {
			a.render(batch);
		}

		for (Fire f : fires) {
			f.render(batch);
		}

		for (Animal a : animals) {
			a.render(batch);
		}

		for (Explosion e : explosions) {
			e.render(batch);
		}

		batch.end();

		batch.begin();
		font.draw(batch, "Go near animal to save", 20, cam.viewportHeight / 2 + 150);
		batch.end();
	}

	public void createDebrisBox2D() {
		MapLayer layer = tileMap.getLayers().get("debris");
		if (layer != null) {
			for (MapObject mo : layer.getObjects()) {

				// get debris position from tile map object layer
				float x = (float) mo.getProperties().get("x", Float.class);
				float y = (float) mo.getProperties().get("y", Float.class);

				// create new debris and add to crap list
				Debris f = new Debris(CreateBox2D.createCircle(game.getWorld(), x, y, 100, false, 1,
						BodyType.StaticBody, "debris", B2DVars.BIT_GROUND, B2DVars.BIT_PLAYER));
				crap.add(f);
			}
		}

		layer = tileMap.getLayers().get("people");
		if (layer != null) {
			for (MapObject mo : layer.getObjects()) {

				// get people position from tile map object layer
				float x = (float) mo.getProperties().get("x", Float.class);
				float y = (float) mo.getProperties().get("y", Float.class);

				// create new person and add to people list
				Person f = new Person(CreateBox2D.createCircle(game.getWorld(), x, y, 15, false, 1,
						BodyType.DynamicBody, "people", B2DVars.BIT_GROUND, B2DVars.BIT_GROUND));
				people.add(f);
			}
		}

		layer = tileMap.getLayers().get("fire");
		if (layer != null) {
			for (MapObject mo : layer.getObjects()) {

				// get fire position from tile map object layer
				float x = (float) mo.getProperties().get("x", Float.class);
				float y = (float) mo.getProperties().get("y", Float.class);

				// create new fire and add to fires list

				Fire f = new Fire(CreateBox2D.createCircle(game.getWorld(), x, y, 15, false, 1, BodyType.StaticBody,
						"fire", B2DVars.BIT_GROUND, B2DVars.BIT_PLAYER));
				f.scl((float) Math.random() * 100);
				fires.add(f);

			}

			layer = tileMap.getLayers().get("animals");
			if (layer != null) {
				for (MapObject mo : layer.getObjects()) {

					// get people position from tile map object layer
					float x = (float) mo.getProperties().get("x", Float.class);
					float y = (float) mo.getProperties().get("y", Float.class);

					// create new person and add to people list
					Animal a = new Animal(CreateBox2D.createCircle(game.getWorld(), x, y, 15, false, 1,
							BodyType.DynamicBody, "people", B2DVars.BIT_GROUND, B2DVars.BIT_GROUND));
					animals.add(a);
				}
			}

			layer = tileMap.getLayers().get("animals");
			if (layer != null) {
				for (MapObject mo : layer.getObjects()) {

					// get people position from tile map object layer
					float x = (float) mo.getProperties().get("x", Float.class);
					float y = (float) mo.getProperties().get("y", Float.class);

					// create new person and add to people list
					Animal a = new Animal(CreateBox2D.createCircle(game.getWorld(), x, y, 15, false, 1,
							BodyType.DynamicBody, "people", B2DVars.BIT_GROUND, B2DVars.BIT_GROUND));
					animals.add(a);
				}
			}

			layer = tileMap.getLayers().get("explosion");
			if (layer != null) {
				for (MapObject mo : layer.getObjects()) {

					// get transport position from tile map object layer
					float x = (float) mo.getProperties().get("x", Float.class);
					float y = (float) mo.getProperties().get("y", Float.class);

					// create new transport

					Explosion e = new Explosion(CreateBox2D.createCircle(game.getWorld(), x, y, 15, false, 1,
							BodyType.StaticBody, "transport", B2DVars.BIT_GROUND, B2DVars.BIT_PLAYER));
					explosions.add(e);
				}

			}

			layer = tileMap.getLayers().get("end");
			if (layer != null) {

				for (MapObject mo : layer.getObjects()) {

					// get transport position from tile map object layer
					float x = (float) mo.getProperties().get("x", Float.class);
					float y = (float) mo.getProperties().get("y", Float.class);

					// create new transport

					transport = new Transport(CreateBox2D.createCircle(game.getWorld(), x, y, 15, false, 1,
							BodyType.StaticBody, "transport", B2DVars.BIT_GROUND, B2DVars.BIT_PLAYER));

				}
			}
		}
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		game.getWorld().destroyBody(transport.getBody());

		for (Explosion e : explosions) {
			game.getWorld().destroyBody(e.getBody());
			;
		}

		if (fires != null) {
			for (Fire f : fires) {
				f.dispose();
				game.getWorld().destroyBody(f.getBody());
			}
		}
		if (crap != null) {
			for (Debris d : crap) {
				d.dispose();
				game.getWorld().destroyBody(d.getBody());
			}
		}
		if (people != null) {
			for (Person p : people) {
				p.dispose();
				game.getWorld().destroyBody(p.getBody());
			}
		}

		tileMap.dispose();
		tmr.dispose();
		b2dr.dispose();
		font.dispose();
		FireFighterGame.res.getMusic("l_3").dispose();
		hud.dispose();

	}

}
