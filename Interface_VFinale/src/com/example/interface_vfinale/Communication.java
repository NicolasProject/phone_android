package com.example.interface_vfinale;


public class Communication
{
	private static final char carDelim = ',';
	private static final int m_nbrParamMax = 3;
	private static final int m_tailleParamMax = 3;
	private static final int m_tailleCmdMax = 2;
	private static final int m_tailleTrameMax = 20;
	private static final String m_cmdBlank = "  ";
	private static final String m_paramBlank = "   ";
	private static final int m_paramVide = -1;
	private static final int ir1 = 1;
	private static final int ir2 = 2;
	private static final int ir3 = 3;
	private static final int us = 4;

	
	private Robot bot;
	
	private Robot.PrivateMethodsFriends robot;
	
	public Communication(Robot myBot)
	{
		bot = myBot;
		bot.giveKeyTo(this);
	}
	
	public void receiveKey(Robot.PrivateMethodsFriends key)
	{
		this.robot = key;
	}
	
	
	private class Trame
	{
		public Trame()
		{
			commande = new char[m_tailleCmdMax + 1];
			tabParam = new char[m_nbrParamMax][m_tailleParamMax + 1];
			
			// on initialise le conteneur de la trame découpée
			for (int i = 0; i < m_tailleCmdMax; i++)
			{
				commande[i] = ' ';
			}
			for (int j = 0; j < m_nbrParamMax; j++)
			{
				for (int i = 0; i < m_tailleParamMax; i++)
				{
					tabParam[j][i] = ' ';
				}
			}
			commande[m_tailleCmdMax] = '\0';
			for (int i = 0; i < m_nbrParamMax; i++)
			{
				tabParam[i][m_tailleParamMax] = '\0';
			}
		}
		
		
		char commande[]; // taille de la commande max : 2
		char tabParam[][]; // taille des parametres max : 3
	}
	
	public Trame traitementRecep(String trameReceive)
	{
		Trame trameSeparee = new Trame();
		
		char trameRecue[] = new char[m_tailleTrameMax + 1];
		/*for (int i = 0; i < m_tailleTrameMax; i++)
			trameRecue[i] = ' ';
		trameRecue[m_tailleTrameMax] = '\0';*/
		
		if (trameReceive.length() > m_tailleTrameMax)
			return trameSeparee;
		
		trameReceive.getChars(0, trameReceive.length(), trameRecue, 0);
		

		/*PRINTD("debut traitement trame");
		PRINTD("f1");*/


		int iBcl = 0;
		int iCptParam = 0;
		int iTamponDebParam = 0;


		// rajouter condition sur trameRecu
		// while (trameRecue[iBcl] != carDelim && trameRecue[iBcl] != '\0' && iBcl < TAILLE_CMD_TOT)
		while (trameRecue[iBcl] != carDelim && iBcl < m_tailleCmdMax && trameRecue[iBcl] != '\0')
		{
			//PRINTD("f2");
			trameSeparee.commande[iBcl] = trameRecue[iBcl];
			iBcl++;
		}

		while (trameRecue[iBcl] != carDelim && trameRecue[iBcl] != '\0')
		{
			iBcl++;
			//PRINTD("f3");
		}

		// On est au caractere de delimitation de la commande
		// PRINTD(commande);
		// PRINTD(trameRecue[iBcl]);

		// on saute ce caractere de separation, sinon on est à la fin de la trame
		// iTamponDebParam correspond ici au premier caractere du 1er parametre
		if (trameRecue[iBcl] != '\0')
			iTamponDebParam = ++iBcl;



		// on place les parametres dans le tableau de parametres
		//PRINTD("f10");
		while (trameRecue[iBcl] != '\0')
		{
			int cptChaine = 0;
			//PRINTD("f11");
			// while (trameRecue[iBcl] != carDelim && trameRecue[iBcl] != '\0' && iBcl < TAILLE_PARAM_TOT + iTamponDebParam)
			while (trameRecue[iBcl] != carDelim && trameRecue[iBcl] != '\0' && iBcl < m_tailleParamMax + iTamponDebParam)
			{
				trameSeparee.tabParam[iCptParam][cptChaine] = trameRecue[iBcl];
				cptChaine++;
				iBcl++;
				//PRINTD("f5");
			}

			// Cette boucle permet d'atteindre le caractere de separation des trames, si ce n'est pas deja fait
			while (trameRecue[iBcl] != carDelim && trameRecue[iBcl] != '\0')
			{
				iBcl++;
				//PRINTD("f6");
			}

			// on saute ce caractere de separation, sinon on est à la fin de la trame
			// iTamponDebParam correspond ici au premier caractere du 2eme parametre
			if (trameRecue[iBcl] != '\0')
				iTamponDebParam = ++iBcl;

			//PRINTD("f7");
			iCptParam++;
		}

		//PRINTD(trameSeparee.commande);
		//PRINTD(trameSeparee.tabParam[0]);
		//PRINTD(trameSeparee.tabParam[1]);
		//PRINTD(trameSeparee.tabParam[2]);
		//PRINTD("fin separation trame");

		return trameSeparee;
	}

	void dispatch(/*Robot robot, */final Trame trameSeparee)
	{
		//PRINTD("start dispatch1");
		int cmd;
		int params[] = new int[m_nbrParamMax];

		// convert to integer
		if (trameSeparee.commande.equals(m_cmdBlank) == false)
			cmd = Integer.parseInt(new String(trameSeparee.commande));
		else
			cmd = m_paramVide;

		for (int i = 0; i < m_nbrParamMax; i++)
		{
			// on test si on a pas renseigne le parametre alors on ne met pas 0 mais, on le met à PARAM_VIDE (-1)
			if (trameSeparee.tabParam[i].equals(m_paramBlank) == false)
				params[i] = Integer.parseInt(new String(trameSeparee.tabParam[i]));
			else
				params[i] = m_paramVide;
		}


		//PRINTD("start dispatch");
		//PRINTD("commande : ");
		//PRINTD(cmd);
		//PRINTD(params[0]);
		//PRINTD(params[1]);
		//PRINTD(params[2]);

		switch (cmd)
		{
		case ir1:
			if (params[0] == m_paramVide)
			{
				robot.setCaptIRArr();
				//PRINTD("at_10");
			}
			else
			{
				robot.setCaptIRArr(params[0] != 0); // conversion de int à bool
				//PRINTD("at_11");
			}
			break;
			
		case ir2:
			if (params[0] == m_paramVide)
			{
				robot.setCaptIRG();
				//PRINTD("at_10");
			}
			else
			{
				robot.setCaptIRG(params[0] != 0); // conversion de int à bool
				//PRINTD("at_11");
			}
			break;
			
		case ir3:
			if (params[0] == m_paramVide)
			{
				robot.setCaptIRD();
				//PRINTD("at_10");
			}
			else
			{
				robot.setCaptIRD(params[0] != 0); // conversion de int à bool
				//PRINTD("at_11");
			}
			break;
			
		case us:
			if (params[0] != m_paramVide)
			{
				robot.setDistance(params[0]);
				//PRINTD("at_10");
			}
			else
			{
				assert false : "error send ultrasonor, parameter is empty - error in dispatch() function";
			}
			break;


		default:
			assert false : "DEFAULT switch - error in dispatch() function";
			break;
		}

	}
}




