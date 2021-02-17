package com.goregoblin.subTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

/* Klasse die einen "Button"für ManaSpells darstellt
 * 
 * der ManaSpell-Button soll aus zwei aufeinander zulaufenden ProgressBars
 * bestehen die sich in der Mitte des Buttons treffen wenn sie voll sind
 * jeder ManaFarbe ist "voll" wenn sie Wert 25 erreicht (ändern wir evtl später)
 * es gibt 4 ManaFarben dessen Farbe die ProgressBar annehmen kann:
 * --> Rot, Grün, Gelb, Blau 
 *  
 *  Jede Farbe hat eine bestimmte Eigenschaft die in ManaSpell vereint werden:
 *  Rot: 5 HP Damage
 *  Grün: 5 HP Heal
 *  Gelb: nächster Effekt x2 (müssen wir speichern; boolean sollte reichen)
 *  Blau: 5 AP (ArmorPoints)
 *    
 */

/**
 * @author niko
 *
 */
public class ManaSpell extends Pane {

	private	HBox boxPBLeft;
	private HBox boxPBRight;
	
	// constructor 1
	public ManaSpell(int manaColorLeft, int manaColorRight) throws Exception{
		/* manaColorNumbers:
		 * Red = 0
		 * Green = 1 
		 * Yellow = 3
		 * Blue = 4
		 */
		
		if (manaColorLeft > 3 || manaColorRight > 3) {
			throw new Exception("Only manaColors from 0-3 allowed: Red = 0| Green = 1| Yellow = 3| Blue = 4");
		}
		
		double elemsWidthIn = 225;
		double elemsHeightIn = 75;
		
		this.setPrefSize(elemsWidthIn*2.5, elemsHeightIn*4);
		this.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
		
		ProgressBar pbLeft =  createManaBar(manaColorLeft, false, elemsWidthIn, elemsHeightIn);
		ProgressBar pbRight =  createManaBar(manaColorRight, true, elemsWidthIn, elemsHeightIn);
		
		boxPBLeft = new HBox();
		boxPBRight = new HBox();
		
		boxPBLeft.getChildren().add(pbLeft);
		boxPBRight.getChildren().add(pbRight);
		
		boxPBLeft.setId("pbBoxLeft");
		boxPBRight.setId("pbBoxRight");
		
		this.getChildren().addAll(boxPBLeft,boxPBRight);
		
		Rectangle mBoxFame = new Rectangle(elemsWidthIn*2, elemsHeightIn, Color.TRANSPARENT);
		mBoxFame.setStrokeWidth(5);
		mBoxFame.setArcWidth(30.0);
		mBoxFame.setArcHeight(20.0);
		
		addColorFrame(mBoxFame);
		mBoxFame.setVisible(false);
		
		
		this.getChildren().add(mBoxFame);
		
		this.setOnMouseEntered(e -> {
			mBoxFame.setVisible(true);
		});
		
		this.setOnMouseExited(e -> {
			mBoxFame.setVisible(false);			
		});
		
		this.setOnMousePressed(e -> {
			System.out.println("Spell activated");
			
			/* hier brauchen wir noch ne Methode die den gewünschten Effekt
			 * zurück gibt
			 * also sowas wie: 5 dmg | +5 HP
			 * --> einfach getter-Methoden? in unserem Konzept hat ja jede Farbe ihre fixe Eigenschaft
			 * 
			 * --> aber vorher testen ob die ProgressBars auch voll sind (boolean)
			 * --> gut wäre auch noch eine Änderung der Pane wenn beide Bars voll sind
			 * 		der Spell also potentiell verwendet werden kann
			 * 
			 */
			
			Map<String, Integer> mapEffect = getSpellEffect();

			if (mapEffect.size() == 1) { // same Effect for both Bars (e.g. RED RED)
				for (Entry<String, Integer> entry : mapEffect.entrySet()) {
					System.out.println("Effect-Type: " + entry.getKey() + "; Value: " + entry.getValue()*2);
				}
			}else {
				for (Entry<String, Integer> entry : mapEffect.entrySet()) {
					System.out.println("Effect-Type: " + entry.getKey() + "; Value: " + entry.getValue());
				}
			}
			
		});
	}
	
	
	
	/**
	 * @param manaColor
	 * @param isRightHanded
	 * @return
	 */
	private ProgressBar createManaBar(int manaColor, boolean isRightHanded, double elemsWidthIn, double elemsHeightIn) {
		
		ProgressBar pb = new ProgressBar(0);
		
		double elemsWidth = elemsWidthIn;
		double elemsHeight = elemsHeightIn;
		double initValue = 0.75;

   	    // eine ProgressBar geht immer von 0-1 weil 1 meint der Prozess ist beendet d.h.
		
		pb.setMinWidth(elemsWidth);
		pb.setMaxWidth(elemsWidth);
		pb.setMinHeight(elemsHeight);
		pb.setMaxHeight(elemsHeight);
		pb.setProgress(initValue);
		
		if (isRightHanded) {
			pb.setRotate(-180);
			pb.setTranslateX(elemsWidth);
		}
		
		
		switch (manaColor) {
		case 0:
			pb.setStyle("-fx-border-color: rgba(0, 0, 0, 1.0);"
					  + "-fx-base: rgba(255, 255, 255, 1.0);"
					  + "-fx-accent: rgba(255, 0, 0, 1.0);");
			pb.setId("pbSpellRed");
			break;
		case 1:
			pb.setStyle("-fx-border-color: rgba(0, 0, 0, 1.0);"
					  + "-fx-base: rgba(255, 255, 255, 1.0);"
					  + "-fx-accent: rgba(0, 255, 0, 1.0);");
			pb.setId("pbSpellGreen");
			break;
		case 2:
			pb.setStyle("-fx-border-color: rgba(0, 0, 0, 1.0);"
					  + "-fx-base: rgba(255, 255, 255, 1.0);"
					  + "-fx-accent: rgba(255, 200, 0, 1.0);"); 
			pb.setId("pbSpellYellow");
			break;
		case 3:
			pb.setStyle("-fx-border-color: rgba(0, 0, 0, 1.0);"
					  + "-fx-base: rgba(255, 255, 255, 1.0);"
					  + "-fx-accent: rgba(0, 0, 255, 1.0);");
			pb.setId("pbSpellBlue");
			break;
		default:
			break;
		}
	
		return pb;
	}
	

	private void addColorFrame(Rectangle rect) {
		// ein Basis-Farbobjekt dem wir einen Listener zuordenen können
		ObjectProperty<Color> baseColor = new SimpleObjectProperty<>();

		// KeyValues definieren
		KeyValue keyValue1 = new KeyValue(baseColor, Color.RED, Interpolator.LINEAR);
		KeyValue keyValue2 = new KeyValue(baseColor, Color.YELLOW, Interpolator.LINEAR);

		// KeyValues den Keyframes zuordnen
		KeyFrame keyFrame1 = new KeyFrame(Duration.ZERO, keyValue1);
		KeyFrame keyFrame2 = new KeyFrame(Duration.millis(500), keyValue2);

		// keyframes an die Timeline (hier haben wir nur 2 können aber auch mehr
		// reinpacken)
		Timeline timelineBtnBlink = new Timeline(keyFrame1, keyFrame2);

		// Listener an baseColor; baseColor ist mit den KeyValues verbunden
		baseColor.addListener((obs, oldColor, newColor) -> { // changed(ObservableValue<? extends Color>, Color, Color)
			// hier eine Color setzen reicht (wir könnte auch oldColor setzen; der Effekt wäre der gleiche)
			rect.setStroke(newColor);
		});

		timelineBtnBlink.setAutoReverse(true);
		timelineBtnBlink.setCycleCount(Animation.INDEFINITE);
		timelineBtnBlink.play();
	}
	
	
	public Map<String,Integer> getSpellEffect(){
		
		 Map<String,Integer> mapSpellEffect = new HashMap<String, Integer>();
		 
		 HBox currHBoxLeft = (HBox) this.lookup("#pbBoxLeft");
		 HBox currHBoxRight = (HBox) this.lookup("#pbBoxRight");
		
		 String idLeft = currHBoxLeft.getChildren().get(0).getId();
		 String idRight = currHBoxRight.getChildren().get(0).getId();
		
		 System.out.println("idLeft: " + idLeft);
		 System.out.println("idRight: " + idRight);
		 
		 mapSpellEffect.put(getBarEffectType(idLeft), getBarEffectAmount(idLeft));
		 mapSpellEffect.put(getBarEffectType(idRight), getBarEffectAmount(idRight));
		 
		 return mapSpellEffect;
	}
	
	
	private String getBarEffectType(String id) {
		
		switch (id) {
			case "pbSpellRed":
				return "DMG";
			case "pbSpellGreen":
				return "HP";
			case "pbSpellYellow":
				return "BST";
			case "pbSpellBlue":
				return "AP";
			default:
				return "";
		}
	}
	
	private Integer getBarEffectAmount(String id) {
		
		switch (id) {
			case "pbSpellRed":
				return getDmg();
			case "pbSpellGreen":
				return getHp();
			case "pbSpellYellow":
				return getPowerUp();
			case "pbSpellBlue":
				return getAp();
			default:
				return 0;
		}
	}
	
	
	private Integer getDmg() {
		return 5;
	}
	
	private Integer getHp() {
		return 5;
	}

	private Integer getAp() {
		return 5;
	}
	
	private Integer getPowerUp() {
		return 1;
	}
	
}


