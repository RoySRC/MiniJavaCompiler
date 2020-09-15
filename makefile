DESTDIR := $(shell pwd)/install
PWD = $(shell pwd)
BUILD_LIBS = $(PWD)/build/libs

.PHONY: install

install:
	@gradle build -x test
	@mkdir -p $(DESTDIR)
	@cp -r src/main/java/lib $(DESTDIR)
	@cp $(BUILD_LIBS)/MiniJavaCompiler.jar $(DESTDIR)
	@cp MiniJavaCompiler $(DESTDIR)
	@chmod +x $(DESTDIR)/MiniJavaCompiler
	@chmod +x run