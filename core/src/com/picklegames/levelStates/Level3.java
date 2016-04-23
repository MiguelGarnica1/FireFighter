package com.picklegames.levelStates;


import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.picklegames.game.FireFighterGame;
import com.picklegames.handlers.dialogue.Dialogue;
import com.picklegames.managers.LevelStateManager;

public class Level3 extends LevelState{
	
	private Dialogue d;
	private BitmapFont font;
	
	public Level3(LevelStateManager lsm) {
		super(lsm);
		// TODO Auto-generated constructor stub
		init();
		FireFighterGame.res.loadTexture("image/Character/momFace.png", "mommy");
		FireFighterGame.res.loadTexture("image/Character/youFace.png", "you");
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		d = new Dialogue("dialogue/dialogue.txt");
		font = new BitmapFont();

	
	}

	@Override
	public void handleInput() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(float dt) {
		d.update(dt);
		if(d.isFinished()) System.out.println("done");
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub
		batch.begin();

			batch.draw(FireFighterGame.res.getTexture("mommy"), 750, 350);
			batch.draw(FireFighterGame.res.getTexture("you"), 55, 350);
//			font.draw(batch, "MOM: " + d.getTop(), 120, 425);
//			font.draw(batch,  "YOU: " + d.getBottom(), 120, 100);
			
			font.draw(batch, d.getName(), 450, 425);
			font.draw(batch, d.getMid(), 400 - d.getMid().length()*1.5f, 400);
		batch.end();
	}

	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		d.dispose();
	}

}
