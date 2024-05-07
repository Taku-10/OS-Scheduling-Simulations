JAVAC = javac
JAVA = java
SRC_DIR = src
BIN_DIR = bin
PACKAGE = barScheduling

JFLAGS = -d $(BIN_DIR) -cp $(SRC_DIR)

CP = $(BIN_DIR)

SOURCES = $(SRC_DIR)/$(PACKAGE)/Barman.java \
          $(SRC_DIR)/$(PACKAGE)/Patron.java \
          $(SRC_DIR)/$(PACKAGE)/DrinkOrder.java \
          $(SRC_DIR)/$(PACKAGE)/SchedulingSimulation.java

all: compile

compile: $(SOURCES)
	$(JAVAC) $(JFLAGS) $^

run:
	$(JAVA) -cp $(CP) $(PACKAGE).SchedulingSimulation

clean:
	rm -f $(BIN_DIR)/$(PACKAGE)/*.class
