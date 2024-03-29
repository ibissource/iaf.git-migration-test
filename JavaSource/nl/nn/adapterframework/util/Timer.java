/*
   Copyright 2013 IbisSource Project

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
/*
 * $Log: Timer.java,v $
 * Revision 1.6  2011-11-30 13:51:48  peter
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.2  2011/10/19 15:02:08  peter
 * do not print versions anymore
 *
 * Revision 1.1  2011/10/19 14:49:44  peter
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.4  2004/03/26 10:42:37  johan
 * added @version tag in javadoc
 *
 * Revision 1.3  2004/03/23 17:08:02  gerrit
 * cosmetic changes
 *
 */
package nl.nn.adapterframework.util;

/**
 * Generic timer ('eierwekker') functionality.
 *
 * Calls a {@link TimerListener#onTimeout() onTimeOut()}-event handler when
 * a certain time has passed. 
 * @version $Id$
 * 
 */
public class Timer {        
	
    private TimerListener listener_;
    private long millisecs_;
    private Thread thread_;

    private class Runner implements Runnable {
        public Runner() {
        }

        public void run() {
            try {
                Thread.sleep(millisecs_);
                listener_.onTimeout();
            }
            catch (InterruptedException ignore) {
            }
        }
    }
    public Timer(TimerListener listener, long millisecs) {
        listener_ = listener;
        millisecs_ = millisecs;       
    }
    public void reset() {
        stop();
        start();
    }
    public void reset(long milliseconds) {
        millisecs_ = milliseconds;
        reset();
    }
    public void start() {
        try
        {
            thread_ = new Thread(new Runner());
            thread_.start();
        }
        catch (Exception error) {
            error.printStackTrace();
        }
    }
    public void stop() {
        if ((thread_ != null) &&
            (Thread.currentThread() != thread_)) {
            while (thread_.isAlive())
            {
                thread_.interrupt();
                try {
                    Thread.sleep(500);
                }
                catch (InterruptedException ignore) {
                }
            }
        }
    }
}
