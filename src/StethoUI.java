
import java.io.*;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import ca.uqac.lif.cep.methods.MethodEvent.MethodCall;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class StethoUI extends Application {

	static String[] arg;
	Scene scene1, scene2;
	File fileTrace = new File("Trace1.txt");
	static File fileSignature = null;
	String jarPth = "";
	
	

	BufferedReader reader = null;
	
	public static void main(String[] args) throws ClassNotFoundException, IOException 
	{

		launch(args);
	}

	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	@Override
	public void start(final Stage primaryStage) throws IOException 
	{
		//FileWriter fileWriter2 = new FileWriter(fileTrace);
		// SCENE1---------------------------------------------------------------------------------------------------------------------
		Label jarPath = new Label();
		jarPath.setWrapText(true);
		jarPath.setMaxWidth(500);
		jarPath.setMaxHeight(10);
		jarPath.setText("Set path to target program's JAR");
		Label destPath = new Label();
		destPath.setWrapText(true);
		destPath.setText("Select Class containing Main: ");
		Button buttonNext = new Button("Next");
		buttonNext.setDisable(true);
		Button buttonLoad = new Button("Load");
		Button buttonCancel2 = new Button("Cancel");
		Button buttonCancel = new Button("Cancel");
		ListView<String> list = new ListView<String>();
		list.setDisable(true);
		ObservableList<String> items = FXCollections.observableArrayList();
		list.maxWidth(100);
		list.maxHeight(100);
		list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() 
		{
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) 
			{
				if (list.getSelectionModel().getSelectedItems().size() > 0)
					buttonNext.setDisable(false);
			}
		});
		GridPane grid = new GridPane();
		grid.setHgap(1);
		grid.setVgap(1);
		HBox hbox = new HBox();
		hbox.setSpacing(30);
		hbox.getChildren().addAll(buttonCancel2, buttonNext);
		HBox hboxJ = new HBox();
		hboxJ.setSpacing(20);
		hboxJ.getChildren().addAll(buttonLoad, jarPath);
		VBox vbox = new VBox();
		vbox.setSpacing(20);
		vbox.setPadding(new Insets(30, 30, 30, 30));
		vbox.getChildren().addAll(hboxJ, destPath, list, hbox);
		scene1 = new Scene(vbox, 800, 600);
		
		primaryStage.setTitle("BP-Security-Monitor");
		primaryStage.setScene(scene1);
		scene1.getStylesheets().add("scene2.css");
		//primaryStage.resizableProperty().setValue(Boolean.FALSE);
		primaryStage.setMaximized(true);
		primaryStage.show();
		// SCENE2---------------------------------------------------------------------------------------------------------------------
		Button refresh = new Button("Refresh");
		Button clear = new Button("Clear");
		TextArea console = new TextArea();
		TextArea output = new TextArea();
		console.setEditable(false);
		output.setEditable(false);
		
		ListView<String> constraint = new ListView<String>();
		ObservableList<String> itemsCon = FXCollections.observableArrayList();
		itemsCon.add("LimitBytesWrittenTotal");
		itemsCon.add("TotalBytesWritten");
		itemsCon.add("CallGraphPipe");
		itemsCon.add("SetCallReturn");
		itemsCon.add("NoExec");
		itemsCon.add("NoClassLoader");
		itemsCon.add("NoListing");
		itemsCon.add("NoReceive");
		itemsCon.add("NoSending");
		itemsCon.add("NoNetwork");
		itemsCon.add("NoObserveTime");
		
		constraint.setItems(itemsCon);
		constraint.setPrefWidth(100);
		constraint.setPrefHeight(200);
		constraint.setMaxHeight(100);
		TextField limit = new TextField();
		limit.setText("0");
		limit.textProperty().addListener(new ChangeListener<String>() 
		{
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) 
			{
				if (limit.getText().trim().equals("")) 
				{
					limit.setText("0");
				}

			}
		});
		// Xcell-----------------------------------------------------------------------------------------
		TextField addSig = new TextField();
		TextField addP = new TextField();
		addP.setMaxWidth(300);
		addP.setDisable(true);
		addSig.setMaxWidth(200);
		Button buttonAddSig = new Button("+");
		buttonAddSig.setDisable(true);
		addSig.textProperty().addListener((observable, oldValue, newValue) -> 
		{
			if (newValue.trim().equals("")) 
			{
				addP.setDisable(true);
				buttonAddSig.setDisable(true);
			} else 
			{
				addP.setDisable(false);
				buttonAddSig.setDisable(false);
			}
		});
		HBox sigBox = new HBox(addSig, addP, buttonAddSig);
		ObservableList<String> siglist = FXCollections.observableArrayList();
		ListView<String> lv = new ListView<>(siglist);
		lv.setCellFactory(param -> new XCell());
		lv.setVisible(false);
		lv.setMaxHeight(200);
		sigBox.setVisible(false);
		// TableView-----------------------------------------------------------------------------------------
		TableView<Trace> table = new TableView<Trace>();
		table.setEditable(true);
		TableColumn type = new TableColumn("Trace Type");
		type.setMinWidth(100);
		type.setCellValueFactory(new PropertyValueFactory<Trace, SimpleStringProperty>("Type"));
		TableColumn returnT = new TableColumn("Return type");
		returnT.setMinWidth(150);
		returnT.setCellValueFactory(new PropertyValueFactory<Trace, String>("returnT"));
		TableColumn signature = new TableColumn("Signature");
		signature.setMinWidth(150);
		signature.setCellValueFactory(new PropertyValueFactory<Trace, String>("signature"));
		TableColumn types = new TableColumn("Parameters Types");
		types.setMinWidth(250);
		types.setCellValueFactory(new PropertyValueFactory<Trace, String>("types"));
		TableColumn values = new TableColumn("Parameters Values");
		values.setMinWidth(250);
		values.setCellValueFactory(new PropertyValueFactory<Trace, String>("values"));
		TableColumn length = new TableColumn("Length");
		length.setMinWidth(50);
		length.setCellValueFactory(new PropertyValueFactory<Trace, String>("length"));
		TableColumn depth = new TableColumn("Level");
		depth.setMinWidth(50);
		depth.setCellValueFactory(new PropertyValueFactory<Trace, String>("depth"));
		TableColumn objectHach = new TableColumn("Object Caller Hach");
		objectHach.setMinWidth(200);
		objectHach.setCellValueFactory(new PropertyValueFactory<Trace, String>("objectHach"));
		TableColumn id = new TableColumn("Id");
		id.setMinWidth(150);
		id.setCellValueFactory(new PropertyValueFactory<Trace, String>("id"));
		TableColumn value = new TableColumn("Returned Value");
		value.setMinWidth(150);
		value.setCellValueFactory(new PropertyValueFactory<Trace, String>("value"));
		table.getColumns().addAll(type, returnT, signature, types, values, length, depth, objectHach, id, value);
		// TabPane------------------------------------------------------------------------------------------
		BorderPane borderPane = new BorderPane();
		TabPane tabPane = new TabPane();
		Tab tabC = new Tab();
		tabC.setText("Trace");
		tabC.setClosable(false);
		tabC.setContent(table);
		Tab tabO = new Tab();
		tabO.setClosable(false);
		tabO.setText("Output");
		tabO.setContent(output);
		tabPane.getTabs().add(tabC);
		tabPane.getTabs().add(tabO);
		SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
		// SCENE2
		// fill---------------------------------------------------------------------------------------------------------------------
		Button buttonAnalyse = new Button("Analyse");
		buttonAnalyse.setDisable(true);
		Button buttonPrevious = new Button("Previous");
		HBox hboxConfig = new HBox();
		HBox hboxJsc2 = new HBox();
		hboxJsc2.getChildren().addAll(buttonCancel, buttonPrevious, buttonAnalyse);
		hboxJsc2.setSpacing(50);
		hboxJsc2.setPadding(new Insets(0, 0, 0, 650));
		borderPane.setCenter(tabPane);
		borderPane.setMaxHeight(200);
		Label lab1 = new Label();
		lab1.setText(" Select Contraints:");
		HBox hboxJsc3 = new HBox();
		hboxJsc3.getChildren().addAll(refresh, clear);
		hboxJsc3.setSpacing(30);
		hboxJsc3.setPadding(new Insets(70, 0, 0, 1100));
		VBox vboxJsc3 = new VBox();
		vboxJsc3.getChildren().addAll(hboxJsc3,borderPane );
		vboxJsc3.setSpacing(0);

		
		Label labLim = new Label();
		labLim.setText(" Limit authorized of Bytes: ");
		limit.setMaxWidth(50);
		hboxConfig.getChildren().addAll(labLim, limit);
		hboxConfig.setVisible(false);
		VBox vbox2 = new VBox(vboxJsc3, lab1, constraint, hboxConfig, sigBox, lv, hboxJsc2);
		vbox2.setSpacing(30);
		scene2 = new Scene(vbox2);
		scene2.getStylesheets().add("scene2.css");
		// ---2-------------------------------------------------------------------------------------------
		constraint.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() 
		{
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) 
			{
				try 
				{// hboxConfig.getChildren().removeAll();
					siglist.clear();
					BufferedReader reader2 = null;
					String text2 = null;
					switch (constraint.getSelectionModel().getSelectedItem()) 
					{
					case "LimitBytesWrittenTotal":
						fileSignature = new File(this.getClass().getResource("Signatures/"+
								constraint.getSelectionModel().getSelectedItem().toString() + "-Signatures.txt").getFile());
						reader2 = new BufferedReader(new FileReader(fileSignature));
						siglist.removeAll();
						while ((text2 = reader2.readLine()) != null) 
						{
							siglist.add(text2);
						}
						reader2.close();
						sigBox.setVisible(true);
						lv.setVisible(true);
						hboxConfig.setVisible(true);
						break; // optional
					case "TotalBytesWritten":
						sigBox.setVisible(true);
						lv.setVisible(true);
						fileSignature = new File(this.getClass().getResource("Signatures/"+
								constraint.getSelectionModel().getSelectedItem().toString() + "-Signatures.txt").getFile());
						reader2 = new BufferedReader(new FileReader(fileSignature));
						siglist.removeAll();
						while ((text2 = reader2.readLine()) != null) 
						{
							siglist.add(text2);
						}
						reader2.close();
						sigBox.setVisible(true);
						lv.setVisible(true);
						hboxConfig.setVisible(false);
						break;
					case "CallGraphPipe":
						sigBox.setVisible(false);
						lv.setVisible(false);
						hboxConfig.setVisible(false);
						break; // optional
					case "SetCallReturn":
						sigBox.setVisible(false);
						lv.setVisible(false);
						hboxConfig.setVisible(false);
						break;	
					default: // Optional
						sigBox.setVisible(true);
						lv.setVisible(true);
						
						fileSignature = new File(this.getClass().getResource("Signatures/"+
								constraint.getSelectionModel().getSelectedItem().toString() + "-Signatures.txt").getFile());
						
						
					/*	fileSignature = new File("/Signatures/"+
								constraint.getSelectionModel().getSelectedItem().toString() + "-Signatures.txt");
						*/reader2 = new BufferedReader(new FileReader(fileSignature));
						siglist.removeAll();
						while ((text2 = reader2.readLine()) != null) 
						{
							siglist.add(text2);
						}
						reader2.close();
						sigBox.setVisible(true);
						lv.setVisible(true);
						hboxConfig.setVisible(false);
						break;
					}
					buttonAnalyse.setDisable(false);
				} catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		});
		// Buttons
		// Actions-----------------------------------------------------------------------------------------
		buttonAnalyse.setOnAction(new EventHandler<ActionEvent>() 
		{
			@Override
			public void handle(ActionEvent arg0) 
			{
				SecurityConstraints sc = new SecurityConstraints();
				String selectedCon = constraint.getSelectionModel().getSelectedItem();
				output.setText("");
				output.appendText("Analysing Trace for " + selectedCon + " constraint...\n");
				long startTime = System.currentTimeMillis();
				switch (selectedCon) {
				case "LimitBytesWrittenTotal":
					float max = Float.parseFloat(limit.getText());
					for (Object o : sc.LimitBytesWrittenTotal(fileTrace, fileSignature, max)) 
					{
						if (o != null) 
						{
							output.appendText("limit exceeded at the Trace line " + o + "\n");
							break;
						}
					}
					break; // optional
				case "TotalBytesWritten":
					for (Object o : sc.TotalBytesWritten(fileTrace, fileSignature)) 
					{
						output.appendText("Bytes written: " + o + "\n");
					}
					break; // optional
				case "CallGraphPipe":
					for (Object o : sc.CallGraphPipe(fileTrace))
					{
						output.appendText("Call Graph: " + o + "\n");
					}
					break; // optional
				case "SetCallReturn":
					for (Object o : sc.SetCallsReturn(fileTrace))
					{
						output.appendText(" Call: " + o +" ID: "+((MethodCall)o).getId()+" Output: "+((MethodCall)o).getreturn()+ "\n");
					}
					break; // optional	
				default:
					for (Object o : sc.custom(fileTrace, fileSignature)) 
					{
						output.appendText("Result: " + o + "\n");
					}						
				}
				long stopTime = System.currentTimeMillis();
				long elapsedTime = stopTime - startTime;
				output.appendText("Finished in: " + elapsedTime + " Millis\n");
				selectionModel.select(1);
			}
		});

		buttonAddSig.setOnAction(new EventHandler<ActionEvent>() 
		{
			@Override
			public void handle(ActionEvent arg0) 
			{
				siglist.add((addSig.getText().trim() + " " + addP.getText()).replaceAll("\\s+", " "));
				try 
				
				{	
					
					if (addP.getText().contains("-1"))
					Files.write(Paths.get(fileSignature.toString()),
							(addSig.getText().trim() + " " + addP.getText().replaceAll("\\s+", "  ")+System.lineSeparator()).getBytes(),
							StandardOpenOption.APPEND);
				else
					Files.write(Paths.get(fileSignature.toString()),
							(addSig.getText().trim() + " " + addP.getText().replaceAll("\\s+", " ")+System.lineSeparator()).getBytes(),
							StandardOpenOption.APPEND);
				} catch (IOException e) {
					// exception handling left as an exercise for the reader
				}
				addP.setText("");
				addSig.setText("");
			}
		});
		
		buttonPrevious.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent arg0) 
			{	
				
				
				primaryStage.setScene(scene1);
				primaryStage.setMaximized(true);
			}
		});
		
		buttonCancel.setOnAction(new EventHandler<ActionEvent>() 
		{
			@Override
			public void handle(ActionEvent arg0) 
			{
				primaryStage.close();
			}
		});
		buttonCancel2.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent arg0) 
			{
				primaryStage.close();
			}
		});
		buttonLoad.setOnAction(new EventHandler<ActionEvent>() 
		{
			@Override
			public void handle(ActionEvent arg0) 
			{
				try 
				{	
					
					items.clear();
					buttonNext.setDisable(true);
					FileChooser fileChooser = new FileChooser();
					FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JAR files (*.jar)",
							"*.jar", "*.jar");
					fileChooser.getExtensionFilters().add(extFilter);
					File file = fileChooser.showOpenDialog(primaryStage);
					if (file != null)
					{
						jarPath.setText(file.getAbsolutePath());
						jarPth = file.getAbsolutePath();
						@SuppressWarnings("resource")
						JarFile jarFile = new JarFile(jarPth);
						Enumeration<JarEntry> e = jarFile.entries();
						while (e.hasMoreElements()) 
						{
							JarEntry je = e.nextElement();
							if (je.isDirectory() || !je.getName().endsWith(".class")) 
							{
								continue;
							}
							String className = je.getName().substring(0, je.getName().length() - 6);
							className = className.replace('/', '.');
							items.add(className + "\n");
						}
					}
					if(!items.isEmpty()) {
					list.setItems(items);
					list.setDisable(false);}
					else
						list.setDisable(true);
				} catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		refresh.setOnAction(event -> 
		{
			BufferedReader reader = null;
			try 
			{
				reader = new BufferedReader(new FileReader(fileTrace));
				String text = null;
				ObservableList<Trace> data = FXCollections.observableArrayList();
				while ((text = reader.readLine()) != null) 
				{
					String[] trcLine = text.split(" // ");
					if (trcLine[0].toString().contains("call")) 
					{
						data.add(new Trace(trcLine[0], trcLine[1], trcLine[2],
								Arrays.toString(Arrays.copyOfRange(trcLine, 3, (2 + (((trcLine.length) - 7) / 2)))),
								Arrays.toString(Arrays.copyOfRange(trcLine, (3 + (((trcLine.length) - 7) / 2)),
										trcLine.length - 5)),
								trcLine[trcLine.length - 4], trcLine[trcLine.length - 3], trcLine[trcLine.length - 2],
								trcLine[trcLine.length - 1], " --- "));
					} else
						data.add(new Trace(trcLine[0], " --- ", " --- ", " --- ", " --- ", " --- ", " --- ", " --- ",
								trcLine[2], trcLine[1]));
				}
				table.setItems(data);
			} catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
		clear.setOnAction(event -> 
		{
			
			try 
			{
				FileWriter writer = new FileWriter(fileTrace);
				writer.write("");
				ObservableList<Trace> data = FXCollections.observableArrayList();
				data.removeAll();
				table.setItems(data);
				writer.close();
			} catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
		buttonNext.setOnAction(event -> 
		{
			try 
			{	
				//fileWriter2.write("");
	
				ObservableList<String> selectedIndices = list.getSelectionModel().getSelectedItems();
				URL[] classLoaderUrls = new URL[] { new URL("file:" + jarPth) };
				URLClassLoader child = new URLClassLoader(classLoaderUrls, this.getClass().getClassLoader());
				Object o = selectedIndices.get(0);
				Class<?> simpleClass = Class.forName(o.toString().trim(), true, child);
				Constructor<?> simpleConstructor = simpleClass.getConstructor();
				Object simpleClassObj = simpleConstructor.newInstance();
				primaryStage.setFullScreen(true);
				reader = new BufferedReader(new FileReader(fileTrace));
				String text = null;
				ObservableList<Trace> data = FXCollections.observableArrayList();
				
				while ((text = reader.readLine()) != null) 
				{
					String[] trcLine = text.split(" // ");
					if (trcLine[0].toString().contains("call")) 
					{
						data.add(new Trace(trcLine[0], trcLine[1], trcLine[2],
								Arrays.toString(Arrays.copyOfRange(trcLine, 3, (2 + (((trcLine.length) - 7) / 2)))),
								Arrays.toString(Arrays.copyOfRange(trcLine, (3 + (((trcLine.length) - 7) / 2)),
										trcLine.length - 5)),
								trcLine[trcLine.length - 4], trcLine[trcLine.length - 3], trcLine[trcLine.length - 2],
								trcLine[trcLine.length - 1], " --- "));
					} else
						data.add(new Trace(trcLine[0], " --- ", " --- ", " --- ", " --- ", " --- ", " --- ", " --- ",
								trcLine[2], trcLine[1]));
				}
				table.setItems(data);
				primaryStage.setScene(scene2);
				//refresh.fire();
			
				// !!!!Method method = simpleClass.getMethod("main", String[].class);
				// !!!String[] params = null;
				// !!!method.invoke(simpleClassObj, (Object) params);
			} catch (Exception e) 
			{
				e.printStackTrace();
			}
		});
	}
	// -------------------------------------------------------------------------------------------------------------------
	private static void delF(File inputFile, String lineToRemove) throws IOException 
	{
		File tempFile = new File("myTempFile.txt");
		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
		String currentLine;
		while ((currentLine = reader.readLine()) != null) {
			String trimmedLine = currentLine.trim();
			if (trimmedLine.equals(lineToRemove.trim()))
				continue;
			writer.write(currentLine + System.getProperty("line.separator"));
		}
		writer.close();
		reader.close();
		replaceFile(tempFile, inputFile);
	}

	private static void replaceFile(File inputFile, File outputFile) throws IOException 
	{
		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
		writer.write("");
		String currentLine;
		while ((currentLine = reader.readLine()) != null) 
		{
			writer.write(currentLine + System.getProperty("line.separator"));
		}
		writer.close();
		reader.close();
	}

	@SuppressWarnings("unused")
	private String executeCommand(String command) throws IOException, InterruptedException 
	{
		StringBuffer output = new StringBuffer();
		Process cat = Runtime.getRuntime().exec("java -jar C:/cat.jar C:/test.txt");
		cat.waitFor();
		return output.toString();
	}

	static class XCell extends ListCell<String> 
	{
		HBox hbox = new HBox();
		Label label = new Label("");
		Pane pane = new Pane();
		Button button = new Button("X");

		public XCell() 
		{
			super();
			hbox.getChildren().addAll(label, pane, button);
			HBox.setHgrow(pane, Priority.ALWAYS);
			button.setOnAction(event -> 
			{
				String sigToRem = getItem();
				getListView().getItems().remove(getItem());
				try {
					delF(fileSignature, sigToRem);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
		}

		@Override
		protected void updateItem(String item, boolean empty) 
		{
			super.updateItem(item, empty);
			setText(null);
			setGraphic(null);
			if (item != null && !empty) 
			{
				label.setText(item);
				setGraphic(hbox);
			}
		}
	}

}
