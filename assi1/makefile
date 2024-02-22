# Configurations
PROJECT	=	run
CC	=	g++
FLAGS	=	-g
WFLAGS	=	-Wall -Wextra
OPTIMIZATION =	-O2
OBJ	=	RC6.o main.o
HEADER	=	HEADER.hpp
SRC	=	RC6.cpp
DRIVER	=	main.cpp

# Build
all:		$(PROJECT)

$(PROJECT):	$(OBJ)
		$(CC) $(FLAGS) $^ -o $@

warning-enbl:	$(OBJ)
		$(CC) $(FLAGS) $(WFLAGS) $^ -o $@

optimizations:	$(OBJ)
		$(CC) $(FLAGS) $(OPTIMIZATION) $^ -o $@

# Build object files
%.o:		%.cpp $(HEADER)
		$(CC) $(FLAGS) -c $<

# Clean up
clean:
		rm -f *.o *~ *.a $(PROJECT) warning-enbl optimizations

# tar relevent files
tar:		clean
		cd ..; \
		tar -cvf RC6_enc.tar RC6

rm_tar:
		cd ..; \
		rm RC6_enc.tar
